package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Factory;
import beans.Location;
import dao.FactoryDAO;
import dao.LocationDAO;
import validations.FactoryValidator;

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
			 LocationDAO locationDAO = new LocationDAO(); 
	        // ChocolateDAO chocolateDAO = new ChocolateDAO();
	         //ctx.setAttribute("factoryDAO", new FactoryDAO(ContextPath, locationDAO,chocolateDAO));
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
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Factory findFactory(@PathParam("id") String id) {
		FactoryDAO dao=(FactoryDAO) ctx.getAttribute("factoryDAO");
		return dao.findFactory(id);
	}
	
	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchFactories(@QueryParam("search") String search) {
	    FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
	    Collection<Factory> factories = dao.searchFactories(search);
	    if (factories.isEmpty()) {
	        return Response.status(Response.Status.NO_CONTENT).build();
	    }
	    return Response.ok(factories).build();
	}
	  @GET
	  @Path("/sort")
	  @Produces(MediaType.APPLICATION_JSON)
	  public Response sortFactories(@QueryParam("sortBy") String sortBy, @QueryParam("ascending") boolean ascending) {
	      FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
	      Collection<Factory> factories = dao.sortFactories(sortBy, ascending);
	      return Response.ok(factories).build();
	  }

	  @GET
	  @Path("/filter")
	  @Produces(MediaType.APPLICATION_JSON)
	  public Response filterFactories(@QueryParam("chocolateType") String chocolateType, 
	                                  @QueryParam("chocolateVariety") String chocolateVariety, 
	                                  @QueryParam("openOnly") Boolean openOnly) {
	      FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
	      Collection<Factory> factories = dao.filterFactories(chocolateType, chocolateVariety, openOnly);
	      return Response.ok(factories).build();
	  }
	  
//	  @POST
//	  @Path("/")
//	  @Produces(MediaType.APPLICATION_JSON)
//	  public Response createFactory(Factory factory) {
//		  if (!FactoryValidator.isValidFactory(factory)) {
//	            return Response.status(Response.Status.BAD_REQUEST)
//	                           .entity("Invalid chocolate data")
//	                           .build();
//	        }
//		  
//		  FactoryDAO dao=(FactoryDAO) ctx.getAttribute("factoryDAO");
//		  factory.setIsStatus(true);
//		  factory.setGrade(0);
//		  Factory savedFactory=dao.save(factory);
//	      return Response.status(Response.Status.CREATED).entity(savedFactory).build();
//
//	  }
	  @POST
	  @Path("/")
	  @Produces(MediaType.APPLICATION_JSON)
	  public Response createFactory(Factory factory) {
	      if (!FactoryValidator.isValidFactory(factory)) {
	          return Response.status(Response.Status.BAD_REQUEST)
	                         .entity("Invalid factory data")
	                         .build();
	      }

	      FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");

	      // Provera i čuvanje nove lokacije ako je nepoznata
	      if (factory.getLocation() != null && factory.getLocation().getId() == null) {
	          Location newLocation = dao.getLocationDAO().save(factory.getLocation());
	          factory.setLocation(newLocation);
	      }

	      factory.setIsStatus(true);
	      factory.setGrade(0);
	      Factory savedFactory = dao.save(factory);
	      return Response.status(Response.Status.CREATED).entity(savedFactory).build();
	  }



}
