package services;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addComment(Comment comment) {
        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        comment.setIsRejected(false);
        comment.setIsChecked(false);
        Comment savedComment = dao.save(comment);

        if (savedComment != null) {
            return Response.status(Response.Status.CREATED).entity(savedComment).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add comment").build();
        }
    }
    
    @GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") String id) {
	    CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
	    Comment comment = dao.findComment(id);
	    return Response.ok(comment).build();
	}


	@GET
	@Path("/count/{factoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomerComments(@PathParam("factoryId") int factoryId) {
	    CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
	    Collection<Comment> comments = dao.findCommentsByFactoryAndRejection(factoryId, true);
	    return Response.ok(comments).build();
	}
	@GET
	@Path("/user/{factoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsersComments(@PathParam("factoryId") int factoryId) {
	    CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
	    Collection<Comment> comments = dao.findCommentsByFactory(factoryId);
	    return Response.ok(comments).build();
	}
	@GET
	@Path("/number/{factoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommentsCount(@PathParam("factoryId") int factoryId) {
	    CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
	    Collection<Comment> comments = dao.findCommentsCount(factoryId);
	    return Response.ok(comments).build();
	}
	
	  @PUT
	    @Path("/{id}")
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response updateComment(@PathParam("id") String id, Comment comment) {
	        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
	        Comment updatedComment = dao.updateComment(id, comment);

	        if (updatedComment != null) {
	            return Response.ok(updatedComment).build();
	        } else {
	            return Response.status(Response.Status.NOT_FOUND).entity("Comment not found").build();
	        }
	    }
}

