package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
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


@Path("/chocolates")
public class ChocolateService {
	@Context
	ServletContext ctx;
	
	public ChocolateService() {
	}
	
	@PostConstruct
	public void init() {
		if(ctx.getAttribute("chocolateDAO")==null) {
			String ContextPath=ctx.getRealPath("");
			ctx.setAttribute("chocolateDAO", new ChocolateDAO(ContextPath));
		}
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Chocolate> getProducts() {
		ChocolateDAO dao=(ChocolateDAO) ctx.getAttribute("chocolateDAO");
		return dao.findAll();
	}
	
	@POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createChocolate(Chocolate chocolate) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        chocolate.setImageUri("slika");
        chocolate.setNumberOfChocolates(0); 
        chocolate.setIsOnStock(false); 
        Chocolate savedChocolate = dao.save(chocolate);
        return Response.status(Response.Status.CREATED).entity(savedChocolate).build();
    }
	
	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Chocolate updateChocolate(@PathParam("id") String id,Chocolate chocolate) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.updateChocolate(id, chocolate);
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Chocolate getById(@PathParam("id") String id) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.findChocolates(id);
	}
}
