package com.example.musicplayer.Utils;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;

import java.io.File;
import java.io.IOException;

public class ID3Tags {

    private static Tag findTag(String FilePath) {
        Tag tag;
        try {
            AudioFile f = AudioFileIO.read(new File(FilePath));
            tag = f.getTag();
            return tag;
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

    public static String getLyrics(String filePath) {
        String Lyrics = "";
        Tag mp3Tag = findTag(filePath);
        if (mp3Tag != null)
            Lyrics = mp3Tag.getFirst(FieldKey.LYRICS);
        return Lyrics;
    }

    public static byte[] getBinaryArtwork(String filePath) {
        Tag mp3Tag = findTag(filePath);
        if (mp3Tag.getFirstArtwork() != null)
            return mp3Tag.getFirstArtwork().getBinaryData();
        return new byte[]{0};
    }
}
