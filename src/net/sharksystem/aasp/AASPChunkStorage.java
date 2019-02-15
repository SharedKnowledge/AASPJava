package net.sharksystem.aasp;

import java.io.IOException;
import java.util.List;

/**
 * A storage is a logical unit containing chunks. It offers methods
 * to get (and create) and remove (drop) chunks. There is at least
 * one storage containing message produced by a local app. 
 * 
 * There can be other storages containg messages which arrived from a
 * peer during sychronization.
 * 
 * @author thsc
 */
public interface AASPChunkStorage {

    public AASPChunk getChunk(CharSequence uri, int era) throws IOException;

    public List<AASPChunk> getChunks(int era) throws IOException;

    public void dropChunks(int era) throws IOException;
    
    /**
     * 
     * @param uri chunk storage uri
     * @param fromEra oldest era
     * @param toEra youngest era
     * @return a chunk cache which hides details of era
     * @throws IOException 
     */
    public AASPChunkCache getAASPChunkCache(CharSequence uri, int fromEra, 
            int toEra) throws IOException;
}
