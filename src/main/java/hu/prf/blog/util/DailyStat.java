/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.prf.blog.util;

/**
 *
 * @author Bali
 */
public class DailyStat {
    private String date;
    private int day;
    private int userCount;
    private int unknownCount;

    
    /**
     * @return the day
     */
    public String getDate() {
        return date;
    }

    /**
     * @param day the day to set
     */
    public void setDate(String day) {
        this.date = day;
    }

    /**
     * @return the userCount
     */
    public int getUserCount() {
        return userCount;
    }

    /**
     * @param userCount the userCount to set
     */
    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    /**
     * @return the unknownCount
     */
    public int getUnknownCount() {
        return unknownCount;
    }

    /**
     * @param unknownCount the unknownCount to set
     */
    public void setUnknownCount(int unknownCount) {
        this.unknownCount = unknownCount;
    }

    /**
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(int day) {
        this.day = day;
    }
    
    
}
