package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Factory;
import dao.FactoryDAO;
import dao.LocationDAO;

@Path("/factories")
public class FactoryService {
	@Context
	ServletContext ctx;
	
	public FactoryService() {
	}
	
	@PostConstruct
	public void init() {
		if(ctx.getAttribute("factoryDAO")==null) {
			String ContextPath=ctx.getRealPath("");
			 LocationDAO locationDAO = new LocationDAO(); // Pretpostavimo da LocationDAO ne zahteva parametre u konstruktoru
	         ctx.setAttribute("factoryDAO", new FactoryDAO(ContextPath, locationDAO));
		}
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Factory> getFactories() {
		FactoryDAO dao=(FactoryDAO) ctx.getAttribute("factoryDAO");
		return dao.findAll();
	}
	
}
