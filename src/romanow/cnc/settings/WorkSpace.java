package romanow.cnc.settings;

import com.thoughtworks.xstream.XStream;
import lombok.Getter;
import romanow.cnc.commands.Command;
import romanow.cnc.console.COMPortDriver;
import romanow.cnc.io.I_File;
import romanow.cnc.m3d.M3DOperations;
import romanow.cnc.m3d.M3DVisio;
import romanow.cnc.m3d.ViewAdapter;
import romanow.cnc.m3d.ViewNotifyer;
import romanow.cnc.view.BaseFrame;
import romanow.cnc.utils.Events;
import romanow.cnc.slicer.SliceData;
import romanow.cnc.stl.STLModel3D;
import romanow.cnc.utils.I_Notify;
import romanow.cnc.utils.UNIException;
import romanow.cnc.Values;
import romanow.cnc.view.panels.CNCPopup;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by romanow on 14.02.2018.
 */
public class WorkSpace implements I_File{
    private CopyOnWriteArrayList<BaseFrame> childs = new CopyOnWriteArrayList();
    /** Данные слайсинга */
    private SliceData data=null;
    /** 3D-модель в STL-формате*/
    private STLModel3D model=new STLModel3D();
    /** локальные настройки для данных слайсинга */
    private Settings local=new Settings();
    /** глобальные настройки */
    private Settings global=new Settings();
    /** настройки из импортируемых форматов */
    private Settings temp=new Settings();
    /** Для логирования внутренних ошибок */
    private ViewNotifyer notify=null;
    private ViewAdapter viewCommon = null;
    private JPanel preview=null;
    private M3DOperations operate=null;
    private M3DVisio visio=null;
    public ArrayList<Command> commands = new ArrayList<>();
    /** версия формата файла */
    private int fileFormatVersion=0;
    private UserProfile currentUser = new UserProfile("гость","",Values.userGuest,"");
    /** Последнее явно открытое или сохраненное */
    private String lastName="";
    private int dataState=Values.NoData;
    private int viewMode=Values.PanelLogin;
    private boolean closing=false;
    private COMPortDriver com = new COMPortDriver();
    @Getter private Dimension dim = new Dimension();
    @Getter private double scaleX=1;
    @Getter private double scaleY=1;
    @Getter private double scaleMin=1;
    public void setDimension(){
        if (global.fullScreen){
            dim = Toolkit.getDefaultToolkit().getScreenSize();
            }
        else{
            dim = new Dimension();
            }
        scaleY = dim.width==0 ? 1 : ((double) dim.height)/Values.FrameHeight;
        scaleX = dim.width==0 ? 1 : ((double) dim.width)/Values.FrameWidth;
        scaleMin = scaleX < scaleY ? scaleX : scaleY;
        }
    //--------------------------------------------------------------------------
    public COMPortDriver comPort(){ return com; }
    public void closeApplication(){ 
        closing=true; 
        com.close();
        }
    public int dataState(){ return dataState; }
    public void dataState(int vv){ dataState=vv; }
    public int viewMode(){ return viewMode; }
    public void viewMode(int vv){ viewMode=vv; }
    public void viewModeEnableOne(int vv){ viewMode |=vv; }
    public void viewModeDisableOne(int vv){ viewMode &= ~vv; }
    public boolean isViewModeEnable(int vv){ return (viewMode & vv)!=0; }
    public boolean modelPresent(){ return dataState!=Values.NoData; }
    public boolean slicePresent(){ return dataState==Values.Sliced || dataState==Values.Changed; }
    public boolean sliceChanged(){ return dataState==Values.Changed; }
    public void dataChanged(){ dataState = Values.Changed; }
    public void dataSliced(){ dataState = Values.Sliced; }
    public ViewAdapter viewCommon(){ return viewCommon; }
    public JPanel preview(){ return preview; }
    public M3DOperations operate(){ return operate; }
    public M3DVisio visio(){ return visio; }
    public void preview(JPanel vv){ 
        preview=vv;
        viewCommon.preview(vv);
        }
    //--------------------------------------------------------------------------
    public void noLastName(){ lastName=""; }
    public void lastName(String ss){
        //--- Сохранить полный путь -------------------------------------------
        //int idx = ss.lastIndexOf(".");
        //if (idx!=-1) ss = ss.substring(0,idx);
        //idx = ss.lastIndexOf("\\");
        //if (idx!=-1) ss = ss.substring(idx+1);
        //ss=ss.replace("/", "");
        lastName = ss;
        fileStateChanged();
        }
    public void init(){
        data = null;
        model = new STLModel3D();
        lastName="";
        dataState=Values.NoData;
        }
    public void fileStateChanged(){
        sendEvent(null,Events.FileState,0,0,currentFileTitle(),null);
        }
    public String modelName(){ return !modelPresent() ? "" : model().modelName();}
    public String defaultDir(){ 
        return currentUser.workSpaceDir+(modelName().length()==0 ? "" : (modelName()+"/"));
        }
    public String testDefaultDir(){
        String ss = defaultDir();
        File dir = new File(ss);
        if (!dir.exists()){
            dir.mkdir();
            }
        return ss;
        }
    public String defaultFileName(){
        return lastName.length()!=0 ? lastName : testDefaultDir()+modelName()+"."+Values.MillingFileExtention;
        }
    public String currentFileTitle(){
        return Values.DataStates[dataState]+ " "+modelName() + (!slicePresent() ? "" : " : "+defaultFileName());
        }
    public UserProfile currentUser(){ return currentUser; }
    public void currentUser(UserProfile xx){ currentUser =xx; }
    public int fileFormatVersion(){ return fileFormatVersion; }
    private WorkSpace(){
        //-------------------Общие часы ----------------------------------------
        closing=false;
        new Thread(()->{
            while (!closing){
                try {
                    Thread.sleep(Values.PrinterStateLoopDelay*1000);
                    } catch (InterruptedException e) {}
                sendEvent(null,Events.Clock,0,0,null,null);
                }
            }).start();
        }
    private static WorkSpace one = null;
    /** Ссылка на синглетон */
    public static WorkSpace ws(){
        if (one==null)
            one = new WorkSpace();
        return one;
        }
    public static Settings set(){ return WorkSpace.ws().local(); }
    public void setNotify(ViewNotifyer not, ViewAdapter viewCommon0){
        notify = not;
        viewCommon = viewCommon0;
        operate = new M3DOperations(not);
        visio = new M3DVisio(not,viewCommon);
        }
    public ViewNotifyer getNotify(){
        return notify;
        }
    public void notify(String ss){
        if (notify!=null)
            notify.log(ss);
        }
    public void notify(int mode,String ss){
        if (notify!=null)
            notify.notify(mode,ss);
        }
    public void notifySync(final int mode,final String ss){
        if (notify!=null){
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    notify.notify(mode,ss);
                    }
                });
            }
        }
    public Settings temp(){ return temp; }
    public void temp(Settings set){temp = set; }
    public Settings local(){ return local; }
    public void local(Settings set){ local = set; }
    public Settings global(){ return global; }
    public STLModel3D model() {
        return model;
        }
    public void model(STLModel3D model) {
        this.model = model;
        data = null;
        }
    public Settings settings() {
        return local;
        }
    public void settings(Settings settings) {
        this.local = settings;
        }
    public Settings loadSettings(DataInputStream in) throws IOException {
        String ss = in.readUTF();
        XStream parser=new XStream();
        setAliases(parser);
        Settings set = (Settings) parser.fromXML(ss);
        set.setNotNull();
        return set;
        }
    public void loadModel(String fname, I_Notify notify) throws UNIException{
        local = loadSettings();            // Копия настроек из файла
        model().load(fname, notify);
        local.setZStartFinish();
        local.model.ScaleFactor.setVal(model.normalizedScale());
        data(new SliceData());
        fileStateChanged();        
        dataState = Values.Loaded;
        }
    @Override
    public void load(DataInputStream in) throws IOException {
        fileFormatVersion = in.readInt();
        if (fileFormatVersion < Values.FileFormatVersion)
            notify.log("Устаревший формат файла версия "+fileFormatVersion +", текущая "+Values.FileFormatVersion);
        removeAll();
        local = loadSettings(in);
        STLModel3D model2 = new STLModel3D();
        model2.load(in);
        SliceData data2 = new SliceData();
        data2.load(in,notify);
        data = data2;
        model=model2;
        in.close();
        dataState = Values.Sliced;
        fileStateChanged();
        sendEvent(null,Events.Settings,0,0,null,null);
        sendEvent(null,Events.NewData,0,0,null,null);
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        saveHead(out);
        data.save(out,notify);
        out.close();
        dataState = Values.Sliced;          // Сохранены изменения, если что
        fileStateChanged();
        }
    public void saveSettings(DataOutputStream out, Settings set) throws IOException {
        XStream parser=new XStream();
        setAliases(parser);
        out.writeUTF(parser.toXML(set));
        }
    public void saveHead(DataOutputStream out) throws IOException {
        out.writeInt(Values.FileFormatVersion);
        saveSettings(out,local);
        model.save(out);
        }
    /** Сохранение настроек в XML-файл */
    public void saveSettings(){
        try {
            FileOutputStream out = new FileOutputStream(Values.SettingsFileName);
            OutputStreamWriter is = new OutputStreamWriter(out,"Windows-1251");
            XStream parser=new XStream();
            setAliases(parser);
            parser.toXML(global,is);
            out.close();
            sendEvent(null,Events.Settings,0,0,null,null);
            } catch (Exception e1) {
                if (notify!=null) notify.notify(Values.error,e1.toString());
                }
        }
    public void loadGlobalSettings() throws UNIException{
        global = loadSettings();
        global.setNotNull();
        }
    /** Загрузка настроек из XML-файла */
    public Settings loadSettings() throws UNIException{
        try {
            FileInputStream out = new FileInputStream(Values.SettingsFileName);
            InputStreamReader is = new InputStreamReader(out,"Windows-1251");
            XStream parser=new XStream();
            setAliases(parser);
            Settings set  = (Settings) parser.fromXML(is);
            set.setNotNull();
            out.close();
            return set;
            } catch (Exception e1) {
                throw UNIException.io(e1.toString());
                }
        }
    public void setAliases(XStream parser){
        parser.ignoreUnknownElements();
        parser.allowTypesByWildcard(new String[] {
                "romanow.cnc.settings.**",
            });
        parser.alias("settings",Settings.class);                    // Сининим класса
        parser.alias("account",UserProfile.class);                  // Сининим класса
        parser.useAttributeFor(UserProfile.class, "password");      // В головной тег
        parser.useAttributeFor(UserProfile.class, "accessMode");    // В головной тег
        parser.useAttributeFor(UserProfile.class, "workSpaceDir");  // В головной тег
        parser.useAttributeFor(UserProfile.class, "name");          // В головной тег
        parser.useAttributeFor(IntParameter.class, "min");          // В головной тег
        parser.useAttributeFor(IntParameter.class, "max");          // В головной тег
        parser.useAttributeFor(IntParameter.class, "def");          // В головной тег
        parser.useAttributeFor(IntParameter.class, "val");          // В головной тег
        parser.useAttributeFor(IndexedParameter.class, "idx");      // В головной тег
        parser.useAttributeFor(IndexedParameter.class, "min");      // В головной тег
        parser.useAttributeFor(IndexedParameter.class, "max");      // В головной тег
        parser.useAttributeFor(IndexedParameter.class, "def");      // В головной тег
        parser.useAttributeFor(IndexedParameter.class, "val");      // В головной тег
        parser.useAttributeFor(FloatParameter.class, "min");        // В головной тег
        parser.useAttributeFor(FloatParameter.class, "max");        // В головной тег
        parser.useAttributeFor(FloatParameter.class, "def");        // В головной тег
        parser.useAttributeFor(FloatParameter.class, "val");        // В головной тег
        parser.useAttributeFor(BooleanParameter.class, "def");      // В головной тег
        parser.useAttributeFor(BooleanParameter.class, "val");      // В головной тег
        //pars.addImplicitCollection(Response.class, "userList");   // исключить имя массива из XML
        }
    public void removeAll(){
        model.removeAll();
        data = null;
        dataState=Values.NoData;
        noLastName();
        }
    //---------------------- Межоконные комуникации ---------------------------------------------------------
    public boolean tryToStart(BaseFrame fr){
        for (BaseFrame frame : childs){
            if (fr.getClass()==frame.getClass()){
                frame.toFront();
                fr.dispose();
                return false;
                }
            }
        childs.add(fr);
        fr.setVisible(true);
        return true;
        }
    synchronized public void onClose(BaseFrame fr){
        java.awt.EventQueue.invokeLater(()->{
            childs.remove(fr);
            fr.dispose();
            });
        }
    public void sendEvent(int code){
        sendEvent(null,code,0,0,null,null);
        }
    synchronized public void sendEvent(final BaseFrame frame0, int code,int par1, long par2, String ss, Object oo){
        java.awt.EventQueue.invokeLater(()->{
            for (BaseFrame frame : childs){
                if (frame==frame0)
                    continue;
                frame.onEvent(code,par1,par2,ss,oo);
                }
            });
        }

    //-------------------------------------------- Переменные состояния --------------------------------------
    /** текущий слой печати */
    private int cLayer=0;
    /** Файл SLM3D */
    private String SLM3DFileName=null;
    //------------------------------------------------------------------------
    public SliceData data() {
        return data;
    }
    public void data(SliceData data) {
        this.data = data;
        }
    //------------------------------------------------------------------------
    public int printing() {
        return 0;
        //return global.global.PrintingState.getVal();
        }
    public void printing(int state) {
        sendEvent(null,Events.Print,0,state,"",null);
        //global.global.PrintingState.setVal(state);
        saveSettings();
        }
    public int layerCount() {
        return cLayer;
    }
    public void layerCount(int layerCount) {
        this.cLayer = layerCount;
        sendEvent(null,Events.Layer,0,layerCount,"",null);
        }
    public void testAndCreateDir(String fname){
        int idx1 = fname.lastIndexOf("/");
        int idx2 = fname.lastIndexOf("\\");
        if (idx2 > idx1) idx1 = idx2;
        if (idx1 == -1) return;
        fname = fname.substring(0,idx1+1);
        File ff = new File(fname);
        if (!ff.exists())
            ff.mkdir();
        }
    //------------------------------------------------------------------------------------------------------------------
    public void popup(String mes, boolean longDelay){
        CNCPopup pop = new CNCPopup(mes,longDelay ? Values.PopupMessageDelay : Values.PopupLongDelay);
        }
    public void popup(String mes){
        CNCPopup pop = new CNCPopup(mes,Values.PopupMessageDelay);
    }
}
