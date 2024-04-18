package edu.tongji.backend.util;

import edu.tongji.backend.dto.UserDTO;
import edu.tongji.backend.entity.User;

public class UserHolder {
    private static final ThreadLocal<UserDTO> tl=new ThreadLocal<>();

    public static void saveUser(UserDTO userId){
        tl.set(userId);
    }
    public static UserDTO getUser(){
        return tl.get();
    }
    public static void removeUser(){
        tl.remove();
    }
}
