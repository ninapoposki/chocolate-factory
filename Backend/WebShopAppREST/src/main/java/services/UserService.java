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

import beans.User;
import dao.UserDAO;

import enumerations.Role;


@Path("/users")
public class UserService {
	@Context
	ServletContext ctx;
	
	@PostConstruct
    public void init() {
        if(ctx.getAttribute("userDAO")==null) {
            String ContextPath=ctx.getRealPath("");
            ctx.setAttribute("userDAO", new UserDAO(ContextPath));
        }
    }
	
	public UserService() {
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<User> getUsers() {
		UserDAO dao=(UserDAO) ctx.getAttribute("userDAO");
		return dao.findAll();
	}
	
	@POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(User dto) {
        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setRole(Role.CUSTOMER);
        
        User savedUser = dao.save(user);
        
        if (savedUser != null) {
            return Response.status(Response.Status.CREATED).entity(savedUser).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("User registration failed").build();
        }
    }
	@GET
	 @Path("/exists/{username}")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response checkUsernameExists(@PathParam("username") String username) {
	        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
	        boolean exists = dao.existsByUsername(username);

	        return Response.ok().entity("{\"exists\": " + exists + "}").build();
	    }

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginUser(User loginUser) {
	    UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");

	    String username = loginUser.getUsername();
	    String password = loginUser.getPassword();

	    if (dao.existsByUsername(username)) {
	        boolean validUser = dao.validateUser(username, password);
	        if (validUser) {
	        	User user = dao.findByUsername(username);
	            return Response.ok().entity(user).build();
	           // return Response.ok().entity("{\"message\": \"Login successful\"}").build();
	        } else {
	            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\": \"Invalid password\"}").build();
	        }
	    } else {
	        return Response.status(Response.Status.NOT_FOUND).entity("{\"message\": \"User not found\"}").build();
	    }
	}

	@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User findUserById(@PathParam("id") String id) {
        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
        
        return dao.findUser(id);
    }
	
	@PUT
	 @Path("/update/{id}")
	 @Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(@PathParam("id") String id, User user) {
	     
	     UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
	     User updatedUser = dao.updateUser(id, user);
	     
	     return Response.ok(updatedUser).build();
	 }
	
	//administrator dodaje menadzera-ali moramo dodati i fabriku kojoj pripada tj da postavimo novoj toj fabrici-id usera 
	@POST
    @Path("/addManager")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addManager(User dto) {
        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setRole(Role.MANAGER);
        
        User savedUser = dao.save(user);
        
        if (savedUser != null) {
            return Response.status(Response.Status.CREATED).entity(savedUser).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Adding new manager failed").build();
        }
    }
}
