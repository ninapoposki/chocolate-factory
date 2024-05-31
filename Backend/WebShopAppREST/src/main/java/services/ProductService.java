package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Product;
import dao.ProductDAO;


@Path("/products")
public class ProductService {
	@Context
	ServletContext ctx;
	
	public ProductService() {
	}
	
	@PostConstruct
	public void init() {
		if(ctx.getAttribute("productDAO")==null) {
			String ContextPath=ctx.getRealPath("");
			ctx.setAttribute("productDAO", new ProductDAO(ContextPath));
		}
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Product> getProducts() {
		ProductDAO dao = (ProductDAO) ctx.getAttribute("productDAO");
		return dao.findAll();
	}
	
	
	
}
