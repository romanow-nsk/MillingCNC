/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.settings;

import epos.slm3d.utils.StringCrypter;
import epos.slm3d.utils.Values;

/**
 *
 * @author romanow
 */
public class UserProfile {
    public String name="";
    public String password="";
    public int accessMode=Values.userGuest;
    public String workSpaceDir="";

    public UserProfile(String name0, String password0, int access, String path) {
        name = name0;
        password = StringCrypter.encrypt(password0);
        accessMode = access;
        workSpaceDir = path;
    }
}
