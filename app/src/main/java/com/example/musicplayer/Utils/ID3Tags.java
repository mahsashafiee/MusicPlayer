package com.example.musicplayer.Utils;

import com.example.musicplayer.model.Song;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.IOException;

public class ID3Tags {

    private static Mp3File findMP3(String FilePath) {
        Mp3File mp3File = null;
        try {
            mp3File = new Mp3File(FilePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
        return mp3File;
    }

    public static String getLyrics(String FilePath) {
        String Lyrics = "";
        Mp3File mp3File = findMP3(FilePath);
        Lyrics = mp3File.getId3v2Tag().getLyrics();
        return Lyrics;
    }

    public static void setLyrics(Song song) {

            Mp3File mp3File = findMP3(song.getFilePath());
            mp3File.getId3v2Tag().setLyrics(song.getLyrics());
        try {
            mp3File.save(song.getTitle());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotSupportedException e) {
            e.printStackTrace();
        }
    }

    public static void setTitle(Song song){
        Mp3File mp3File = findMP3(song.getFilePath());
        mp3File.getId3v2Tag().setTitle(song.getTitle());
    }

    public static void setAlbum(Song song){
        Mp3File mp3File = findMP3(song.getFilePath());
        mp3File.getId3v2Tag().setAlbum(song.getAlbum());
    }
}
