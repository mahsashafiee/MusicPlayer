package com.example.musicplayer.Utils;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.IOException;

public class ID3Tags {

    public static String getLyrics(String FilePath){
        String Lyrics = new String();
        try {
            Mp3File mp3File = new Mp3File(FilePath);
            Lyrics = mp3File.getId3v2Tag().getLyrics();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
        return Lyrics;
    }

    public static void setLyrics(String FilePath , String Lyrics){

        try {
            Mp3File mp3File = new Mp3File(FilePath);
            mp3File.getId3v2Tag().setLyrics(Lyrics);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    }
}
