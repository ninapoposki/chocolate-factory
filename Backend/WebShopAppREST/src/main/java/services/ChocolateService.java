package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
	
}
