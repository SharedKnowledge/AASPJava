package net.sharksystem.asap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ASAPEngine that stores data in file system.
 * @author thsc
 */
public class ASAPEngineFS extends ASAPEngine {
    public static final String MEMENTO_FILENAME = "aaspCurrentAttributes";
    private final String rootDirectory;
    
    public static final String DEFAULT_ROOT_FOLDER_NAME = "SHARKSYSTEM_AASP";
    
    private ASAPEngineFS(String rootDirectory,
                         ASAPChunkStorageFS chunkStorage)
        throws ASAPException, IOException {
        
        super(new ASAPChunkStorageFS(rootDirectory));
        
        this.rootDirectory = rootDirectory;
    }
    
    public static ASAPStorage getASAPStorage(String owner, String rootDirectory)
            throws IOException, ASAPException {
        
        // check if root directory already exists. If not srt it up
        File root = new File(rootDirectory);
        if(!root.exists()) {
            root.mkdirs();
        }
        
        return ASAPEngineFS.getASAPEngine(owner, rootDirectory);
        
    }
    
    public static ASAPStorage getASAPStorage(String rootDirectory)
            throws IOException, ASAPException {
        
        return ASAPEngineFS.getASAPStorage(DEFAULT_OWNER, rootDirectory);
        
    }
    
    public static ASAPEngine getASAPEngine(String owner, String rootDirectory)
            throws IOException, ASAPException {
        
        // root directory must exist when setting up an engine
        File root = new File(rootDirectory);
        if(!root.exists() || !root.isDirectory()) {
            throw new ASAPException("chunk root directory must exist when creating an ASAPEngine");
        }
        
        ASAPEngineFS engine = new ASAPEngineFS(
                rootDirectory, 
                new ASAPChunkStorageFS(rootDirectory));

        
        ASAPMementoFS mementoFS = engine.getMemento(rootDirectory);
        engine.memento = mementoFS;
        
        mementoFS.restore(engine);
        
        // reset owner?
        if(owner != null && engine.owner != null
                && !engine.owner.equalsIgnoreCase(ANONYMOUS_OWNER)
                && !engine.owner.equalsIgnoreCase(DEFAULT_OWNER)
                && !owner.equalsIgnoreCase(ANONYMOUS_OWNER)
                && !owner.equalsIgnoreCase(DEFAULT_OWNER)
                && !owner.equalsIgnoreCase(engine.owner)) {
            
                    throw new ASAPException("cannot overwrite folder of a "
                        + "a non-anonymous different owner: " + engine.owner);
        } 
        
        // replacing owner could be done
        if(owner != null
                && !owner.equalsIgnoreCase(ANONYMOUS_OWNER)
                && !owner.equalsIgnoreCase(DEFAULT_OWNER)
        ) {
            engine.owner = owner;
            mementoFS.save(engine);
        }
        
        return engine;
    }

    public static ASAPEngine getASAPEngine(String rootDirectory)
            throws IOException, ASAPException {
            
        return ASAPEngineFS.getASAPEngine(null, rootDirectory);

    }
    
    private ASAPMementoFS getMemento(String rootDirectory) {
        return new ASAPMementoFS(new File(rootDirectory));
    }
    
    private HashMap<CharSequence, ASAPChunkStorage> storageList = new HashMap<>();

    @Override
    public ASAPChunkStorage getIncomingChunkStorage(CharSequence sender) {
        String dir = this.rootDirectory + "/" + sender;
        return new ASAPChunkStorageFS(dir);
    }

    @Override
    public List<CharSequence> getSender() {
        List<CharSequence> senderList = new ArrayList<>();

        File dir = new File(this.rootDirectory);

        String[] dirEntries = dir.list();

        if (dirEntries != null) {
            for (String fileName : dirEntries) {
                // era folder?
                try {
                    Integer.parseInt(fileName);
                    // a number. It is a era folder go ahead
                    continue;
                } catch (NumberFormatException e) {
                    // no number - that's ok!
                }

                File fileInDir = new File(this.rootDirectory + "/" + fileName);
                if (fileInDir.isDirectory()) {
                    senderList.add(fileName);
                }
            }
        }

        return senderList;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //                                         helper                                     //
    ////////////////////////////////////////////////////////////////////////////////////////

    public static void removeFolder(String eraPathName) {
        File dir = new File(eraPathName);

        String[] dirEntries = dir.list();

        if(dirEntries != null) {
            for(String fileName : dirEntries) {
                File fileInDir = new File(eraPathName + "/" + fileName);
                if(fileInDir.isDirectory()) {
                    ASAPEngineFS.removeFolder(fileInDir.getAbsolutePath());
                } else {
                    try {
                        fileInDir.delete();
                    } catch (RuntimeException e) {
                        System.err.println("ASAPEngineFS: cannot file:" + e.getLocalizedMessage());
                        // try next
                    }
                }
            }
        }

        dir.delete();
    }
}