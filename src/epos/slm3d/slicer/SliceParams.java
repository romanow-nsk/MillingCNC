package epos.slm3d.slicer;

/**
 * Created by romanow on 26.10.2018.
 */
public class SliceParams {
    public final int mode;
    // mode=0 - новый слой при слайсинге
    // mode=1 - новый слой при редактировании
    // mode=2 - повторный слайсинг с новым сечением
    // mode=3 - повторный слайсинг с новыс сечением и контурами
    public SliceLayer layer;
    public int layerNum;
    public double zOrig;
    public SliceParams(int mode, SliceLayer layer0, int layerNum0, double zOrig0){
        this.mode = mode;
        layerNum=layerNum0;
        zOrig=zOrig0;
        layer=layer0;
        }
    public SliceParams(int mode, SliceLayer layer0, int layerNum0){
        this(mode,layer0,layerNum0,-1);
        }
    public SliceParams(int layerNum0, double z2){
        this(1, null,layerNum0,z2);
        }
    public SliceParams(int layerNum0){
        this(0, null,layerNum0,-1);
        }
}
