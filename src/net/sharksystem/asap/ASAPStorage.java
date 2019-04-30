package net.sharksystem.asap;

import java.io.IOException;
import java.util.List;

/**
 *
 * Communication break down in ad-hoc networks is normal and no failure.
 * That chunk storage is meant to keep messages which are produced by an
 * app for later transmission.
 * 
 * Messages which cannot be sent to their recipients can be stored in AASP chunks.
 * Each chunk is addressed with an URI (comparable to URIs e.g. in Android
 * Content Provider)
 * 
 * Applications can easlily store their messages by calling add(URI, message).
 * That message is stored in a chunk addressed by the URI. 
 * 
 * Each chunk has a recipient list which can be changed anytime. The ASAPEngine
 * uses those information for sending such stored messages whenever a peer
 * establishes a connection.
 * 
 * It is recommended to use ASAPEngineFS to set up that framework.
 * Create a ASAPEngine like this
 * 
 * <pre>
 * AASPReader reader = ...;
 * ASAPStorage myStorage = ASAPEngineFS.getASAPEngine("EngineName", "ChunkStorageRootFolder", reader);
 * </pre>
 * 
 * An AASPReader must be implemented prior using that framework. Objects of
 * that class are called whenever another peer transmits messages to the
 * local peer. @see AASPReader
 * 
 * Chunks are structured by eras. In most cases, application developers don't 
 * have to care about era management at all. If so, take care. Eras are usually 
 * changed by the ASAPEngine whenever a peer (re-) connects. In that case, the
 * current era is declared to be finished and an new era is opened. 
 * Any new message is now tagged as message from that new era. The ASAPEngine
 * transmitts all message to the peer which are stored after the final 
 * encounter. If no encounter ever happend - all avaiable messages are 
 * transmitted. 
 *
 * @see ASAPEngine
 *
 * @author thsc
 */
public interface ASAPStorage {

    
    /**
     * Adds a recipient to chunk recipient list.
     * @param urlTarget chunk address
     * @param recipient recipient to add
     * @throws IOException 
     */
    public void addRecipient(CharSequence urlTarget, CharSequence recipient) throws IOException;

    /**
     /**
     * Set a list of recipients for chunk. A former list is dropped.
     * 
     * @param urlTarget chunk address
     * @param recipients new list of recipients
     * @throws IOException 
     */
    public void setRecipients(CharSequence urlTarget, List<CharSequence> recipients) throws IOException;

    /**
     * Removes recipients
     * @param urlTarget chunk address
     * @param recipients list of recipients to be removed
     * @throws IOException 
     */
    public void removeRecipient(CharSequence urlTarget, CharSequence recipients) throws IOException;
    
    /**
     * Add a message to that chunk.
     * @param urlTarget chunk address
     * @param message Message to be kept for later transmission
     * @throws IOException 
     */
    public void add(CharSequence urlTarget, CharSequence message) throws IOException;
    
    /**
     * Create a new era
     */
    public void newEra();
    
    /**
     * Get oldest era available on that peer.
     * @return 
     */
    public int getOldestEra();
    
    /**
     * Get current era.
     * @return 
     */
    public int getEra();
    
    /**
     * Get next era number. Era numbers are organized in a circle. Number 0
     * follows Integer.MAXVALUE. That method takes care of that fact.
     * No change is made on current era.
     * 
     * @param era
     * @return 
     */
    public int getNextEra(int era);
    
    /**
     * Get previous era number. Era numbers are organized in a circle. Er 
     * number 0 is proceeded by era number Integer.MAXVALUE. 
     * That method takes care of that fact.
     * No change is made on current era.
     * 
     * @param era
     * @return 
     */
    public int getPreviousEra(int era);
    
    /**
     * Default behaviour of ASAPEngine: Each peer / communication partner
     * gets its own chunk storage. That storage is filled during asap
     * synchronization. That storage can be retrieved with this command.
     * 
     * @param sender
     * @return 
     */
    public ASAPChunkStorage getIncomingChunkStorage(CharSequence sender);

    /**
     *
     * @return list of peers with an incoming chunk storage
     */
    public List<CharSequence> getSender();

    /**
     * 
     * @return The local chunk storage that is meant to be used by the local
     * app. Note: That storage is changed during an AASP session.
     */
    public ASAPChunkStorage getChunkStorage();
}