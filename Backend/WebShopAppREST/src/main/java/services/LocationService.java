package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Location;
import dao.LocationDAO;


@Path("/locations")
public class LocationService {
	@Context
	ServletContext ctx;
	
	public LocationService() {
	}
	
	@PostConstruct
	public void init() {
		if(ctx.getAttribute("locationDAO")==null) {
			String ContextPath=ctx.getRealPath("");
			ctx.setAttribute("locationDAO", new LocationDAO(ContextPath));
		}
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Location> getLocations() {
		LocationDAO dao=(LocationDAO) ctx.getAttribute("locationDAO");
		return dao.findAll();
	}
	
}
