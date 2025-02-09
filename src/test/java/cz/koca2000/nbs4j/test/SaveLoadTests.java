package cz.koca2000.nbs4j.test;

import cz.koca2000.nbs4j.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SaveLoadTests {

    static Song originalSong;

    @BeforeAll
    static void prepareSong(){
        originalSong = new Song()
                .addLayer(new Layer()
                        .setName("Test layer 1")
                        .setVolume(50)
                        .setPanning(50)
                        .setLocked(true))
                .addLayer(new Layer()
                        .setName("Test layer 2")
                        .setVolume(25)
                        .setPanning(-50))
                .setNote(5, 0, new Note()
                        .setInstrument(6)
                        .setVolume(20)
                        .setKey(70)
                        .setPitch(10))
                .setNote(10, 1, new Note()
                        .setInstrument(1, true)
                        .setVolume(40)
                        .setKey(30)
                        .setPitch(-10))
                .addCustomInstrument(new CustomInstrument()
                        .setName("Custom Instrument 1")
                        .setFileName("file/name")
                        .setKey(10)
                        .setShouldPressKey(true))
                .setTempoChange(-1, 8.0f);
        originalSong.freezeSong();
    }

    @ParameterizedTest
    @EnumSource
    void layerName(NBSVersion nbsVersion){
        Song savedSong = saveAndLoad(originalSong, nbsVersion);

        assertEquals(originalSong.getLayersCount(), savedSong.getLayersCount());
        for (int i = 0; i < originalSong.getLayersCount(); i++){
            assertEquals(originalSong.getLayer(i).getName(), savedSong.getLayer(i).getName());
        }
    }

    @ParameterizedTest
    @EnumSource
    void layerVolume(NBSVersion nbsVersion){
        Song savedSong = saveAndLoad(originalSong, nbsVersion);

        assertEquals(originalSong.getLayersCount(), savedSong.getLayersCount());
        for (int i = 0; i < originalSong.getLayersCount(); i++){
            assertEquals(originalSong.getLayer(i).getVolume(), savedSong.getLayer(i).getVolume());
        }
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = { "V1" })
    void layerPanning(NBSVersion nbsVersion){
        Song savedSong = saveAndLoad(originalSong, nbsVersion);

        assertEquals(originalSong.getLayersCount(), savedSong.getLayersCount());
        for (int i = 0; i < originalSong.getLayersCount(); i++){
            assertEquals(originalSong.getLayer(i).getPanning(), savedSong.getLayer(i).getPanning());
        }
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = { "V1", "V2", "V3" })
    void layerIsLocked(NBSVersion nbsVersion){
        Song savedSong = saveAndLoad(originalSong, nbsVersion);

        assertEquals(originalSong.getLayersCount(), savedSong.getLayersCount());
        for (int i = 0; i < originalSong.getLayersCount(); i++){
            assertEquals(originalSong.getLayer(i).isLocked(), savedSong.getLayer(i).isLocked());
        }
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = { "V1", "V2", "V3" })
    void layerContainsSameNotes(NBSVersion nbsVersion){
        Song savedSong = saveAndLoad(originalSong, nbsVersion);

        assertEquals(originalSong.getLayersCount(), savedSong.getLayersCount());
        int nextTickOriginal = originalSong.getNextNonEmptyTick(-1);
        int nextTickSaved = savedSong.getNextNonEmptyTick(-1);

        while (nextTickOriginal == nextTickSaved && nextTickOriginal != -1) {
            for (int i = 0; i < originalSong.getLayersCount(); i++) {
                Note noteOriginal = originalSong.getLayer(i).getNote(nextTickOriginal);
                Note noteSaved = savedSong.getLayer(i).getNote(nextTickSaved);

                assertEquals(noteOriginal != null, noteSaved != null);

                if (noteOriginal == null || noteSaved == null)
                    continue;

                assertEquals(noteOriginal.getInstrument(), noteSaved.getInstrument());
                assertEquals(noteOriginal.isCustomInstrument(), noteSaved.isCustomInstrument());
                assertEquals(noteOriginal.getKey(), noteSaved.getKey());
                assertEquals(noteOriginal.getPanning(), noteSaved.getPanning());
                assertEquals(noteOriginal.getPitch(), noteSaved.getPitch());
                assertEquals(noteOriginal.getVolume(), noteSaved.getVolume());
            }
            nextTickOriginal = originalSong.getNextNonEmptyTick(nextTickOriginal);
            nextTickSaved = savedSong.getNextNonEmptyTick(nextTickSaved);
        }
        assertEquals(nextTickOriginal, nextTickSaved);
    }

    @ParameterizedTest
    @EnumSource
    void customInstrumentName(NBSVersion nbsVersion){
        Song savedSong = saveAndLoad(originalSong, nbsVersion);

        assertEquals(originalSong.getCustomInstrumentsCount(), savedSong.getCustomInstrumentsCount());
        for (int i = 0; i < originalSong.getCustomInstrumentsCount(); i++){
            assertEquals(originalSong.getCustomInstrument(i).getName(), savedSong.getCustomInstrument(i).getName());
        }
    }

    @ParameterizedTest
    @EnumSource
    void customInstrumentFileName(NBSVersion nbsVersion){
        Song savedSong = saveAndLoad(originalSong, nbsVersion);

        assertEquals(originalSong.getCustomInstrumentsCount(), savedSong.getCustomInstrumentsCount());
        for (int i = 0; i < originalSong.getCustomInstrumentsCount(); i++){
            assertEquals(originalSong.getCustomInstrument(i).getFileName(), savedSong.getCustomInstrument(i).getFileName());
        }
    }

    @ParameterizedTest
    @EnumSource
    void customInstrumentPitch(NBSVersion nbsVersion){
        Song savedSong = saveAndLoad(originalSong, nbsVersion);

        assertEquals(originalSong.getCustomInstrumentsCount(), savedSong.getCustomInstrumentsCount());
        for (int i = 0; i < originalSong.getCustomInstrumentsCount(); i++){
            assertEquals(originalSong.getCustomInstrument(i).getKey(), savedSong.getCustomInstrument(i).getKey());
        }
    }

    @ParameterizedTest
    @EnumSource
    void customInstrumentPressKey(NBSVersion nbsVersion){
        Song savedSong = saveAndLoad(originalSong, nbsVersion);

        assertEquals(originalSong.getCustomInstrumentsCount(), savedSong.getCustomInstrumentsCount());
        for (int i = 0; i < originalSong.getCustomInstrumentsCount(); i++){
            assertEquals(originalSong.getCustomInstrument(i).shouldPressKey(), savedSong.getCustomInstrument(i).shouldPressKey());
        }
    }

    @ParameterizedTest
    @EnumSource
    void tempo(NBSVersion nbsVersion){
        Song savedSong = saveAndLoad(originalSong, nbsVersion);

        assertEquals(originalSong.getTempo(0), savedSong.getTempo(0));
    }

    @ParameterizedTest
    @EnumSource
    void lengthInTicks(NBSVersion nbsVersion) {
        Song savedSong = saveAndLoad(originalSong, nbsVersion);

        assertEquals(originalSong.getSongLength(), savedSong.getSongLength());
    }

    private static Song saveAndLoad(Song song, NBSVersion nbsVersion){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        song.save(nbsVersion, outputStream);
        return Song.fromStream(new ByteArrayInputStream(outputStream.toByteArray()));
    }

}
