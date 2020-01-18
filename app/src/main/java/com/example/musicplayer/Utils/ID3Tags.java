package com.example.musicplayer.Utils;

import android.util.Log;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;

import java.io.File;
import java.io.IOException;

public class ID3Tags {

    private static String TAG = "ID3TAG";

    private static AudioFile getAudioFile(String FilePath) {
        try {
            return AudioFileIO.read(new File(FilePath));

        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void setFile(AudioFile audioFile) {
        try {
            //AudioFileIO.write(audioFile);
            audioFile.commit();
        } catch (CannotWriteException e) {
            e.printStackTrace();
        }

    }

    public static String getLyrics(String filePath) {
        String Lyrics = "";
        try {
            Tag mp3Tag = getAudioFile(filePath).getTag();
            if (mp3Tag != null) {
                Lyrics = mp3Tag.getFirst(FieldKey.LYRICS);
            }
        } catch (Exception e) {
            Log.d(TAG, "getLyrics: " + e.getMessage());
        }
        return Lyrics;
    }

    public static Artwork getArtwork(String filePath) {
        try {
            Tag mp3Tag = getAudioFile(filePath).getTag();
            if (mp3Tag != null)
                return mp3Tag.getFirstArtwork();

        } catch (Exception e) {
            Log.d(TAG, "getArtwork: " + e.getMessage());
        }
        return null;
    }

    public static void setLyrics(String filePath, String lyrics) {
        AudioFile audioFile = getAudioFile(filePath);
        try {
            Tag mTag = audioFile.getTag();
            mTag.deleteField(FieldKey.LYRICS);
            mTag.setField(FieldKey.LYRICS, lyrics);
            setFile(audioFile);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        } catch (NullPointerException e2) {
            Log.d(TAG, "setLyrics: fail");
        }
    }

    public static void setAlbum(String filePath, String album) {
        AudioFile audioFile = getAudioFile(filePath);
        Tag mTag = audioFile.getTag();
        try {
            mTag.deleteField(FieldKey.ALBUM);
            mTag.setField(FieldKey.ALBUM, album);
            setFile(audioFile);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        }
    }

    public static void setArtist(String filePath, String artist) {
        AudioFile audioFile = getAudioFile(filePath);
        Tag mTag = audioFile.getTag();
        try {
            mTag.deleteField(FieldKey.ARTIST);
            mTag.setField(FieldKey.ARTIST, artist);
            setFile(audioFile);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        }
    }

    public static void setTitle(String filePath, String title) {
        AudioFile audioFile = getAudioFile(filePath);
        Tag mTag = audioFile.getTag();
        try {
            mTag.deleteField(FieldKey.TITLE);
            mTag.setField(FieldKey.TITLE, title);
            setFile(audioFile);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        }
    }

    public static void setArtwork(String filePath, Artwork artwork) {
        AudioFile audioFile = getAudioFile(filePath);
        Tag mTag = audioFile.getTag();
        try {
            mTag.deleteField(FieldKey.COVER_ART);
            mTag.setField(artwork);
            setFile(audioFile);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
        }
    }
}
