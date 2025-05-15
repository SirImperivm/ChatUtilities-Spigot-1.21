package me.sirimperivm.chatUtilities.assets.others;

@SuppressWarnings("all")
public class Strings {
    
    public static int getCountOf(String string, String phrase) {
        int count = 0;
        int index = 0;
        while ((index = string.indexOf(phrase, index)) != -1) {
            count++;
            index += phrase.length();
        }
        return count;
    }
}