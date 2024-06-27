package services;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import beans.ShoppingCart;
import dao.ShoppingCartDAO;
import dao.UserDAO;
import dto.ShoppingCartDTO;
import dao.ChocolateDAO;
import dao.FactoryDAO;
import dao.LocationDAO;

@Path("/shoppingCarts")
public class ShoppingCartService {
    @Context
    ServletContext ctx;

    public ShoppingCartService() {
    }

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("shoppingCartDAO") == null) {
            String contextPath = ctx.getRealPath("");
            LocationDAO locationDAO = new LocationDAO(contextPath); 
            UserDAO userDAO=new UserDAO();
	        ChocolateDAO chocolateDAO = new ChocolateDAO(contextPath);
            FactoryDAO factoryDAO = new FactoryDAO(contextPath, locationDAO,chocolateDAO,userDAO);
            
            factoryDAO.loadFactories(contextPath); 
            ctx.setAttribute("factoryDAO", factoryDAO); 
            ShoppingCartDAO shoppingCartDAO = new ShoppingCartDAO(contextPath, chocolateDAO);
            ctx.setAttribute("shoppingCartDAO", shoppingCartDAO); 
        }
    }

    @GET
    @Path("/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ShoppingCart> getByUserId(@PathParam("userId") int userId) {
        ShoppingCartDAO dao = (ShoppingCartDAO) ctx.getAttribute("shoppingCartDAO");
        return dao.findByCustomerId(userId);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ShoppingCart> getShoppingCarts() {
        ShoppingCartDAO dao = (ShoppingCartDAO) ctx.getAttribute("shoppingCartDAO");
        return dao.findAll();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createShoppingCart(ShoppingCart shoppingCart) {
        ShoppingCartDAO dao = (ShoppingCartDAO) ctx.getAttribute("shoppingCartDAO");
        shoppingCart.setState(true);
        ShoppingCart savedShoppingCart = dao.save(shoppingCart);
        return Response.status(Response.Status.CREATED).entity(savedShoppingCart).build();
    }
    
    @GET
    @Path("/user/{userId}/ids")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getShoppingCartIdsByUserId(@PathParam("userId") int userId) {
        ShoppingCartDAO dao = (ShoppingCartDAO) ctx.getAttribute("shoppingCartDAO");
        return dao.findShoppingCartIdsByCustomerId(userId);
    }
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteShoppingCart(@PathParam("id") String id) {
        ShoppingCartDAO dao = (ShoppingCartDAO) ctx.getAttribute("shoppingCartDAO");
        ShoppingCart shoppingCart = dao.findShoppingCart(id);
        if (shoppingCart != null) {
            dao.deleteShoppingCart(id);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    @PUT
    @Path("/changeQuantity")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeChocolateQuantity(ShoppingCartDTO itemDTO) {
        ShoppingCartDAO dao = (ShoppingCartDAO) ctx.getAttribute("shoppingCartDAO");
        dao.changeChocolateQuantity(itemDTO);
        return Response.ok().build();
    }
}
