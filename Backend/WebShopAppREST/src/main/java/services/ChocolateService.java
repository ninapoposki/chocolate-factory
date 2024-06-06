package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Chocolate;
import dao.ChocolateDAO;
import dao.FactoryDAO;
import dao.LocationDAO;

import validations.ChocolateValidator;

@Path("/chocolates")
public class ChocolateService {
	@Context
	ServletContext ctx;
	
	public ChocolateService() {
	}
	
	@PostConstruct
	public void init() {
	    if (ctx.getAttribute("chocolateDAO") == null) {
	        String contextPath = ctx.getRealPath("");
	        LocationDAO locationDAO = new LocationDAO(contextPath); 
	        FactoryDAO factoryDAO = new FactoryDAO(contextPath, locationDAO);
	        factoryDAO.loadFactories(contextPath); 
	        ctx.setAttribute("factoryDAO", factoryDAO); 
	        ChocolateDAO chocolateDAO = new ChocolateDAO(contextPath, factoryDAO);
	        ctx.setAttribute("chocolateDAO", chocolateDAO); 
	    }
	}

	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Chocolate> getProducts() {
		ChocolateDAO dao=(ChocolateDAO) ctx.getAttribute("chocolateDAO");
		return dao.findAll();
	}
	
	/*@POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createChocolate(Chocolate chocolate) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        chocolate.setImageUri("slika");
        chocolate.setNumberOfChocolates(0); 
        chocolate.setIsOnStock(false); 
        chocolate.setIsActive(true);
        Chocolate savedChocolate = dao.save(chocolate);
        return Response.status(Response.Status.CREATED).entity(savedChocolate).build();
    }*/
	 @POST
	    @Path("/")
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response createChocolate(Chocolate chocolate) {
	        if (!ChocolateValidator.isValidChocolate(chocolate)) {
	            return Response.status(Response.Status.BAD_REQUEST)
	                           .entity("Invalid chocolate data")
	                           .build();
	        }

	        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
	        //chocolate.setImageUri("slika");
	        chocolate.setIsOnStock(false);  //da li fali set is active?     
	        chocolate.setNumberOfChocolates(0); 
	        chocolate.setIsActive(true);
	        Chocolate savedChocolate = dao.save(chocolate);
	        return Response.status(Response.Status.CREATED).entity(savedChocolate).build();
	    }
	
	/*@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Chocolate updateChocolate(@PathParam("id") String id,Chocolate chocolate) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.updateChocolate(id, chocolate);
	}*/
	 //probicaaaaaaaaaaaaa
	 @PUT
	 @Path("/{id}")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response updateChocolate(@PathParam("id") String id, Chocolate chocolate) {
	     if (!ChocolateValidator.isValidChocolate(chocolate)) {
	         return Response.status(Response.Status.BAD_REQUEST)
	                 .entity("Invalid chocolate data")
	                 .build();
	     }
	     ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
	     Chocolate updatedChocolate = dao.updateChocolate(id, chocolate);
	     
	     return Response.ok(updatedChocolate).build();
	 }

	
	@GET
	@Path("/choco/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Chocolate getById(@PathParam("id") String id) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.findChocolates(id);
	}

	 @GET
	 @Path("/{id}")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Collection<Chocolate> getChocolatesByFactoryId(@PathParam("id") String factoryId) {
	        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
	        return dao.findChocolatesByFactoryId(factoryId);
	  }
	 
	 @DELETE
	 @Path("/{id}")
	 @Produces(MediaType.APPLICATION_JSON)
	 public void deleteChocolate(@PathParam("id") String id) {
	     ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
	     dao.deleteChocolate(id);
	 }

}
