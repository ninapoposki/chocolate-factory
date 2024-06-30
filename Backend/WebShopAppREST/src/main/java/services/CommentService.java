package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import dao.CommentDAO;
import beans.Comment;

@Path("/comments")
public class CommentService{
	@Context
	ServletContext ctx;
	
	public CommentService() {
	}
	
	@PostConstruct
	public void init() {
		if(ctx.getAttribute("commentDAO")==null) {
			String contextPath=ctx.getRealPath("");
			//verovatno user i factory dao mora ovde jos
			CommentDAO commentDAO=new CommentDAO(contextPath);
			ctx.setAttribute("commentDAO", commentDAO);
		}
	}
	
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Comment> getComments(){
		CommentDAO dao=(CommentDAO) ctx.getAttribute("commentDAO");
		return dao.findAll();
	}
}

