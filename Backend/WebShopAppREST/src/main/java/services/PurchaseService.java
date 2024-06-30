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
import beans.Purchase;
import beans.ShoppingCart;
import dao.PurchaseDAO;
import dao.ShoppingCartDAO;
import dao.UserDAO;
import enumerations.PurchaseStatus;
import dao.ChocolateDAO;
import dao.FactoryDAO;
import dao.LocationDAO;
import javax.annotation.PostConstruct;// ova dva
import javax.json.bind.annotation.JsonbTransient;
import validations.ChocolateValidator;

@Path("/purchases")
public class PurchaseService {
	@Context
	ServletContext ctx;
	
	public PurchaseService() {
	}
	
//	@PostConstruct
//	public void init() {
//	    if (ctx.getAttribute("purchaseDAO") == null) {
//	        String contextPath = ctx.getRealPath("");
//	        LocationDAO locationDAO = new LocationDAO(contextPath); 
//	        FactoryDAO factoryDAO = new FactoryDAO(contextPath, locationDAO);
//	        ChocolateDAO chocolateDAO = new ChocolateDAO(contextPath, factoryDAO);
//	        factoryDAO.loadFactories(contextPath); 
//	        ctx.setAttribute("factoryDAO", factoryDAO); 
//	        PurchaseDAO purchaseDAO = new PurchaseDAO(contextPath, factoryDAO, chocolateDAO);
//	        ctx.setAttribute("purchaseDAO", purchaseDAO); 
//	    }
//	}
	@PostConstruct

	public void init() {
	    if (ctx.getAttribute("purchaseDAO") == null) {
	        String contextPath = ctx.getRealPath("");
	        LocationDAO locationDAO = new LocationDAO(contextPath); 
	        UserDAO userDAO=new UserDAO();
	        ChocolateDAO chocolateDAO = new ChocolateDAO(contextPath);
	        FactoryDAO factoryDAO = new FactoryDAO(contextPath, locationDAO, chocolateDAO,userDAO);
	        
	        factoryDAO.loadFactories(contextPath); 
	        ctx.setAttribute("factoryDAO", factoryDAO);
	        ShoppingCartDAO shoppingCartDAO = new ShoppingCartDAO(contextPath, chocolateDAO);
	        PurchaseDAO purchaseDAO = new PurchaseDAO(contextPath, factoryDAO, chocolateDAO, shoppingCartDAO);
	        ctx.setAttribute("purchaseDAO", purchaseDAO); 
	    }
	}
	/*

    public void init() {
        if (ctx.getAttribute("purchaseDAO") == null) {
            String contextPath = ctx.getRealPath("");
            LocationDAO locationDAO = new LocationDAO(contextPath);
            UserDAO userDAO=new UserDAO();//dodala
            //ili ovako: proveri sa docom UserDAO userDAO=new UserDAO(contextPath)
            ChocolateDAO chocolateDAO = new ChocolateDAO(contextPath);
            FactoryDAO factoryDAO = new FactoryDAO(contextPath, locationDAO, chocolateDAO,userDAO); //dopunila
            chocolateDAO.setFactoryDAO(factoryDAO); // Postavite FactoryDAO u ChocolateDAO

            factoryDAO.loadFactories(contextPath);

            ctx.setAttribute("factoryDAO", factoryDAO);
            ctx.setAttribute("chocolateDAO", chocolateDAO);

            PurchaseDAO purchaseDAO = new PurchaseDAO(contextPath, factoryDAO, chocolateDAO);
            ctx.setAttribute("purchaseDAO", purchaseDAO);
        }
    }
*/

	@GET
	@Path("/user/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Purchase> getByUserId(@PathParam("userId") int userId) {
	    PurchaseDAO dao = (PurchaseDAO) ctx.getAttribute("purchaseDAO");
	    return dao.findByCustomerId(userId);
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Purchase> getPurchases() {
		PurchaseDAO dao=(PurchaseDAO) ctx.getAttribute("purchaseDAO");
		return dao.findAll();
	}
	
	
	 @POST
	    @Path("/")
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response createPurchase(Purchase purchase) {
	       

	        PurchaseDAO dao = (PurchaseDAO) ctx.getAttribute("purchaseDAO");
	        
	        ShoppingCartDAO shoppingCartDAO = (ShoppingCartDAO) ctx.getAttribute("shoppingCartDAO");

	        // Preuzimanje ShoppingCart objekta na osnovu customer ID iz Purchase objekta
	        Collection<ShoppingCart> shoppingCarts = shoppingCartDAO.findByCustomerId(purchase.getCustomerId());

	        for (ShoppingCart shoppingCart : shoppingCarts) {
	            // Ažuriranje state polja na true u ShoppingCart objektu
	            shoppingCart.setState(false); // Postavljanje state na true

	            // Čuvanje ažuriranog ShoppingCart objekta
	            shoppingCartDAO.updateShoppingCart(shoppingCart.getId(), shoppingCart);
	        }
	        
	        purchase.setStatus(PurchaseStatus.PROCESSING);
	        Purchase savedPurchase = dao.save(purchase);
	        return Response.status(Response.Status.CREATED).entity(savedPurchase).build();
	    }
	
	  @PUT
	    @Path("/cancel/{id}")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response cancelPurchase(@PathParam("id") String id) {
	        PurchaseDAO dao = (PurchaseDAO) ctx.getAttribute("purchaseDAO");
	        Purchase updatedPurchase = dao.updatePurchaseStatus(id, PurchaseStatus.CANCELLED);
	        if (updatedPurchase == null) {
	            return Response.status(Response.Status.NOT_FOUND).build();
	        }
	        return Response.ok(updatedPurchase).build();
	    }
}
