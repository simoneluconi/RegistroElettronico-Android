package com.sharpdroid.registro;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class ClasseVivaRestAPI {
    static void AggiornaVoti() {
        // stub
    }

    public class DownloadVoti {
        public List<Voto> parseJSON(InputStream inputStream) throws IOException {
            List<Voto> voti = new LinkedList<>();
        /* Parse JSON */
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
            reader.setLenient(true); // strip XSSI protection
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                Voto nuovoVoto = new Voto();
                while (reader.hasNext()) {
                    switch (reader.nextName()) {
                        case "q":
                            nuovoVoto.setQ(reader.nextString());
                            break;
                        case "ns":
                            nuovoVoto.setBlu(reader.nextBoolean());
                            break;
                        case "type":
                            nuovoVoto.setTipo(reader.nextString());
                            break;
                        case "date":
                            nuovoVoto.setData(reader.nextString());
                            break;
                        case "mark":
                            nuovoVoto.setVoto(reader.nextDouble());
                            break;
                        case "desc":
                            nuovoVoto.setCommento(reader.nextString());
                            break;
                        default:
                            reader.skipValue();
                    }
                }
                reader.endObject();
            }
            return voti;
        }
    }
}
