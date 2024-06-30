package services;

import java.io.StringReader;
import java.util.Collection;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
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
import dao.UserDAO;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import validations.ChocolateValidator;

@Path("/chocolates")
public class ChocolateService {
	@Context
	ServletContext ctx;
	
	public ChocolateService() {
	}
	
//	@PostConstruct
//	public void init() {
//	    if (ctx.getAttribute("chocolateDAO") == null) {
//	        String contextPath = ctx.getRealPath("");
//	        LocationDAO locationDAO = new LocationDAO(contextPath); 
//	        FactoryDAO factoryDAO = new FactoryDAO(contextPath, locationDAO);
//	        factoryDAO.loadFactories(contextPath); 
//	        ctx.setAttribute("factoryDAO", factoryDAO); 
//	        ChocolateDAO chocolateDAO = new ChocolateDAO(contextPath, factoryDAO);
//	        ctx.setAttribute("chocolateDAO", chocolateDAO); 
//	    }
//	}
	@PostConstruct
    public void init() {
        if (ctx.getAttribute("chocolateDAO") == null) {
            String contextPath = ctx.getRealPath("");
            LocationDAO locationDAO = new LocationDAO();
            UserDAO userDAO=new UserDAO(); //dodala
            ChocolateDAO chocolateDAO = new ChocolateDAO(contextPath);
            ctx.setAttribute("chocolateDAO", chocolateDAO);

            FactoryDAO factoryDAO = new FactoryDAO(contextPath, locationDAO, chocolateDAO,userDAO); //dopunila
            ctx.setAttribute("factoryDAO", factoryDAO);

            chocolateDAO.setFactoryDAO(factoryDAO);
            chocolateDAO.loadChocolates(contextPath);
            factoryDAO.loadFactories(contextPath);
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
	        chocolate.setIsOnStock(false);  //nema je na stanju kad se napravi     
	        chocolate.setNumberOfChocolates(0); //nema kolicine
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
	 
	 @GET
	 @Path("/types")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Set<String> getChocolateTypes(){
		 ChocolateDAO dao=(ChocolateDAO) ctx.getAttribute("chocolateDAO");
		 return dao.getAllChocolateTypes();
	 }
	 
	 @GET
	 @Path("/varieties")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Set<String> getChocolateVarieties(){
		 ChocolateDAO dao=(ChocolateDAO) ctx.getAttribute("chocolateDAO");
		 return dao.getAllChocolateVarieties();
		 
	 }
	 
	 @PATCH
	 @Path("/quantity/{id}")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response updateChocolateQuantity(@PathParam("id") String id, String quantityJson) {
	     ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
	     Chocolate chocolate = dao.findChocolates(id);
	     if (chocolate == null) {
	         return Response.status(Response.Status.NOT_FOUND).entity("Chocolate not found").build();
	     }

	     try {
	         JsonObject jsonObject = Json.createReader(new StringReader(quantityJson)).readObject();
	         int quantity = jsonObject.getInt("numberOfChocolates");

	         chocolate.setNumberOfChocolates(quantity);
	         chocolate.setIsActive(true);
	         chocolate.setIsOnStock(true);
	         dao.updateChocolate(id, chocolate);
	         return Response.ok(chocolate).build();
	     } catch (JsonException | NumberFormatException e) {
	         return Response.status(Response.Status.BAD_REQUEST).entity("Invalid quantity format").build();
	     }
	 }

	 
	
	 

}
