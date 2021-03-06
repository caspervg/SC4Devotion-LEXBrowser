package net.caspervg.lex4j.route;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.caspervg.lex4j.auth.Auth;
import net.caspervg.lex4j.bean.Comment;
import net.caspervg.lex4j.bean.DependencyList;
import net.caspervg.lex4j.bean.DependencyString;
import net.caspervg.lex4j.bean.Lot;
import net.caspervg.lex4j.log.LEX4JLogger;
import org.restlet.data.Disposition;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * Provides routing options for the <i>Lot</i> endpoint
 * @see <a href="http://restlet.org/learn/javadocs/2.1/jse/api/org/restlet/data/Status.html">Restlet Status API Javadoc</a>
 * @see <a href="https://github.com/caspervg/SC4Devotion-LEX-API/blob/master/Lot.md">LEX API Overview on Github</a>
 */
public class LotRoute {

    private Auth auth;
    private ObjectMapper mapper;

    /**
     * Creates a new LotRoute, without Authorisation token. Some functions may not work!
     */
    public LotRoute() {
        this(null);
    }

    /**
     * Creates a new LotRoute, with Authorisation token.
     * @param auth Authentication token
     */
    public LotRoute(Auth auth) {
        this.auth = auth;
        this.mapper = new ObjectMapper();
    }

    /**
     * Retrieve the lot/file
     * @param id ID of the lot to be retrieved
     * @return Lot
     * @see ExtraLotInfo
     */
    public Lot getLot(int id) {
        return getLot(id, new ExtraLotInfo.PublicExtraInfo());
    }

    /**
     * Retrieve the lot/file
     * @param id ID of the lot to be retrieved
     * @return Lot
     * @see ExtraLotInfo
     */
    public Lot getLot(int id, ExtraLotInfo extra) {
        ClientResource resource = new ClientResource(Route.LOT.url(id));
        Route.handleExtraInfo(resource, extra, auth);

        try {
            Representation repr = resource.get();
            return mapper.readValue(repr.getText(), Lot.class);
        } catch (IOException e) {
            LEX4JLogger.log(Level.WARNING, "Could not retrieve lot correctly!");
            return null;
        }
    }

    /**
     * Retrieve a list of lots/files.
     * Note that this method will only set the {@link Lot#getId()} and {@link Lot#getName()} properties
     * correctly because of API limitations.
     *
     * @return List of lots/files
     */
    public List<Lot> getLotList() {
        ClientResource resource = new ClientResource(Route.ALL_LOT.url());

        try {
            Representation repr = resource.get();
            return mapper.readValue(repr.getText(), new TypeReference<List<Lot>>(){});
        } catch (IOException ex) {
            LEX4JLogger.log(Level.WARNING, "Could not retrieve lot list correctly!");
            return null;
        }
    }

    /**
     * Downloads a lot/file
     * @param id ID of the lot/file
     * @param directory Directory where the file should be downloaded
     * @custom.require Authentication
     * @return Download succeeded
     */
    public boolean getLotDownload(int id, File directory) {
        ClientResource resource = new ClientResource(Route.DOWNLOAD_LOT.url(id));
        resource.setChallengeResponse(this.auth.toChallenge());

        try {
            Representation repr = resource.get(MediaType.APPLICATION_ZIP);
            Disposition disposition = repr.getDisposition();

            File file = new File(directory, disposition.getFilename());
            FileOutputStream fos = new FileOutputStream(file);
            repr.write(fos);

            // Flush remaining buffer to output and close the stream
            fos.flush();
            fos.close();

            return true;
        } catch (IOException ex) {
            LEX4JLogger.log(Level.WARNING, "Could not write download to the FileOutputStream!");
            return false;
        }
    }

    /**
     * Adds a file to the user's download list
     * @param id : ID of the lot/file
     * @custom.require Authentication
     */
    public void putLotDownloadList(int id) {
        ClientResource resource = new ClientResource(Route.DOWNLOADLIST_LOT.url(id));
        resource.setChallengeResponse(this.auth.toChallenge());

        resource.get();
    }

    /**
     * Removes a file from the user's download list
     * @param id : ID of the lot/file
     * @custom.require Authentication
     */
    public void deleteLotDownloadList(int id) {
        ClientResource resource = new ClientResource(Route.DOWNLOADLIST_LOT.url(id));
        resource.setChallengeResponse(this.auth.toChallenge());

        resource.delete();
    }

    /**
     * Retries the list of comments for a lot/file
     * @param id ID of the lot/file
     * @return List of comments
     */
    public List<Comment> getComment(int id) {
        ClientResource resource = new ClientResource(Route.GET_COMMENT.url(id));
        try {
            String result = resource.get().getText();
            return mapper.readValue(result, new TypeReference<List<Comment>>() {});
        } catch (IOException ex) {
            LEX4JLogger.log(Level.WARNING, "Could not retrieve lot comments correctly!");
            return null;
        }
    }

    /**
     * Adds a comment and/or rating to a lot/file
     * @param id ID of the lot/file
     * @param rating rating for the lot/file (can be null)
     * @param comment comment for the lot/file (can be null)
     * @custom.require Authentication
     */
    public void postComment(int id, int rating, String comment) {
        ClientResource resource = new ClientResource(Route.ADD_COMMENT.url(id));
        resource.setChallengeResponse(this.auth.toChallenge());

        Form form = new Form();
        if (id > 0)
            form.add("rating", String.valueOf(rating));
        if (comment != null && comment.length() > 0)
            form.add("comment", comment);

        resource.post(form);
    }

    /**
     * Retrieves the dependency list for a lot/file
     * @deprecated use {@link LotRoute#getDependencyList} instead
     * @param id ID of the lot/file
     * @return DependencyList
     */
    public DependencyList getDependency(int id) {
        return getDependencyList(id);
    }


    /**
     * Retrieves the dependency list for a lot/file
     * @param id ID of the lot/file
     * @return DependencyList
     */
    public DependencyList getDependencyList(int id) {
        ClientResource resource = new ClientResource(Route.DEPENDENCY.url(id));

        try {
            Representation repr = resource.get();
            return mapper.readValue(repr.getText(), DependencyList.class);
        } catch (IOException ex) {
            LEX4JLogger.log(Level.WARNING, "Could not retrieve lot dependencies correctly!");
            return null;
        }
    }

    /**
     * Updates the dependency string for a lot/file
     * @param id ID of the lot/file
     * @param dep new dependency string
     * @custom.require Authentication and Administration
     */
    public void updateDependencyString(int id, String dep) {
        ClientResource resource = new ClientResource(Route.DEPENDENCY_STRING.url(id));
        Reference ref = resource.getReference();

        Form form = new Form();
        form.add("string", dep);

        resource.setChallengeResponse(this.auth.toChallenge());
        resource.put(form);
    }

    /**
     * Retrieves the dependency string for a lot/file
     * @param id ID of the lot/file
     * @return the dependency string
     */
    public String getDependencyString(int id) {
        ClientResource resource = new ClientResource(Route.DEPENDENCY_STRING.url(id));

        try {
            Representation repr = resource.get();
            return mapper.readValue(repr.getText(), DependencyString.class).getDependency();
        } catch (IOException ex) {
            LEX4JLogger.log(Level.WARNING, "Could not retrieve lot dependency string correctly!");
            return null;
        }
    }

}
