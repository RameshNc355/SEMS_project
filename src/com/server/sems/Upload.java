package com.server.sems;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.server.sems.utility.Helper;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
@Path("/upload")
public class Upload {
	@POST
	@Path("/file")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	
	public String uploadFile(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("name") String fileName) {

		String uploadedFileLocation = "c://Temp_Tomcat/" + fileName;

		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);

		String output = "File uploaded to :" + uploadedFileLocation;
		
		String response = Helper.constructJSON("upload file", true, uploadedFileLocation);
		return response;//Response.status(200).entity(output).build();

	}

	// save uploaded file to new location
	private void writeToFile(InputStream  uploadedInputStream,
		String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}


}
