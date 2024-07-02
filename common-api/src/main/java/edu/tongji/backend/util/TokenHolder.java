package edu.tongji.backend.util;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */




import edu.tongji.backend.dto.UserDTO;

public class TokenHolder {
    private static final ThreadLocal<String> tl=new ThreadLocal<>();

    public static void saveToken(String token){
        tl.set(token);
    }
    public static String getToken(){
        return tl.get();
    }
    public static void removeToken(){
        tl.remove();
    }
}
