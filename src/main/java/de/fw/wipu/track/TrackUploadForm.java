package de.fw.wipu.track;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class TrackUploadForm {

    @FormParam("metadata")
    @PartType(MediaType.APPLICATION_JSON)
    public TrackInput metadata;

    @FormParam("gpx")
    @PartType("application/gpx+xml")
    public FileUpload gpxFile;


}
