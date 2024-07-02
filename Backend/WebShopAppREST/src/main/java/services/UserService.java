package services;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; //dodala
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.servlet.http.Cookie;
import java.util.UUID;
import beans.Chocolate;


import beans.User;
import dao.UserDAO;
import dto.RegisteredUserDTO;
import javax.ws.rs.core.NewCookie;

import enumerations.ActivityStatus;
import enumerations.Role;


@Path("/users")
public class UserService {
	@Context
	ServletContext ctx;
	
	 @Context
	    HttpSession session;
	
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
        user.setPoints(0);
        
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

	 @GET
	    @Path("/getByUsername/{username}")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response getUserByUsername(@PathParam("username") String username) {
	        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
	        User user = dao.findByUsername(username);

	        if (user != null) {
	            return Response.ok().entity(user).build();
	        } else {
	            return Response.status(Response.Status.NOT_FOUND).entity("User not found for username: " + username).build();
	        }
	    }
	
	/*@POST
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
*/
	
	 @POST
	 @Path("/login")
	 @Consumes(MediaType.APPLICATION_JSON)
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response loginUser(User loginUser, @Context HttpServletRequest request, @Context HttpServletResponse response) {
	     UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");

	     String username = loginUser.getUsername();
	     String password = loginUser.getPassword();

	     if (dao.existsByUsername(username)) {
	         boolean validUser = dao.validateUser(username, password);
	         if (validUser) {
	             User user = dao.findByUsername(username);
	             
	             // Provera da li je korisnik deaktiviran
	             if (user.getActivity() == ActivityStatus.DEACTIVATED) {
	                 return Response.status(Response.Status.FORBIDDEN).entity("{\"message\": \"Your account is blocked and you cannot log in.\"}").build();
	             }

	             request.getSession().setAttribute("loginUser", user);
	             Cookie userCookie = new Cookie("username", user.getUsername());
	             userCookie.setPath("/");
	             response.addCookie(userCookie);
	             
	             Cookie roleCookie = new Cookie("userRole", user.getRole().toString());
	             roleCookie.setPath("/");
	             response.addCookie(roleCookie);
	             
	             Cookie idCookie = new Cookie("id", user.getId());
	             idCookie.setPath("/");
	             response.addCookie(idCookie);
	             
	             return Response.ok().entity(user).build();
	         } else {
	             return Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\": \"Invalid password\"}").build();
	         }
	     } else {
	         return Response.status(Response.Status.NOT_FOUND).entity("{\"message\": \"User not found\"}").build();
	     }
	 }


	
	@POST
	@Path("/logout")
	public void logoutUser( @Context HttpServletRequest request, @Context HttpServletResponse response) {
       	
	        	request.getSession().invalidate();
	        	Cookie userCookie = new Cookie("username", null );
	        	userCookie.setMaxAge(0);
	        	userCookie.setPath("/");
	        	response.addCookie(userCookie);
	        	
	        	Cookie roleCookie = new Cookie("userRole", null);
	        	roleCookie.setMaxAge(0);
	        	roleCookie.setPath("/");
	        	response.addCookie(roleCookie);
	       
	        	Cookie idCookie = new Cookie("id", null);
	        	idCookie.setMaxAge(0);
	        	idCookie.setPath("/");
	        	response.addCookie(idCookie);
	}

	
	
/*	@POST
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
	            
	            // Generiši jedinstveni session ID
	            String sessionId = UUID.randomUUID().toString();
	            user.setSessionId(sessionId);
	            
	            // Sačuvaj session ID u bazi podataka ako je potrebno
	            dao.updateUser(user.getId(), user);
	            
	            // Kreiraj kolačić sa session ID-om
	            NewCookie cookie = new NewCookie("session_id", sessionId, "/", null, null, 86400, false);
	            
	            return Response.ok().entity(user).cookie(cookie).build();
	        } else {
	            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\": \"Invalid password\"}").build();
	        }
	    } else {
	        return Response.status(Response.Status.NOT_FOUND).entity("{\"message\": \"User not found\"}").build();
	    }
	}*/
	

	
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
        user.setPoints(0);
        
        User savedUser = dao.save(user);
        
        if (savedUser != null) {
            return Response.status(Response.Status.CREATED).entity(savedUser).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Adding new manager failed").build();
        }
    }
	
	@GET
	@Path("/withoutAdm")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllWithoutAdministrators() {
	    UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
	    Collection<User> users = dao.getAllWithoutAdministrators();
	    if (users == null || users.isEmpty()) {
	        return Response.status(Response.Status.NOT_FOUND).build();
	    } else {
	        return Response.status(Response.Status.OK).entity(users).build();
	    }
	}
//	@GET
//	@Path("/search")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response searchUsers(@QueryParam("searchTerm") String searchTerm) {
//	    UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
//	    Collection<User> users = dao.searchUsers(searchTerm);
//	    return Response.ok(users).build();
//	}
//	@GET
//	@Path("/sort")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response sortUsers(@QueryParam("sortBy") String sortBy, @QueryParam("ascending") boolean ascending) {
//	    UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
//	    Collection<User> users = dao.sortUsers(sortBy, ascending);
//	    return Response.ok(users).build();
//	}
//
//	//filter za tip korisnika??
//	@GET
//	@Path("/filter")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response filterUsers(@QueryParam("role") String role, @QueryParam("type") String type) {
//	    UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
//	    Collection<User> users = dao.filterUsers(role, type);
//	    return Response.ok(users).build();
//	}

	@GET
	@Path("/searchSortFilter")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchSortFilterUsers(
	        @QueryParam("searchTerm") String searchTerm,
	        @QueryParam("sortBy") String sortBy,
	        @QueryParam("ascending") boolean ascending,
	        @QueryParam("role") String role,
	        @QueryParam("type") String type) {
	    UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
	    Collection<User> users = dao.searchSortFilterUsers(searchTerm, sortBy, ascending, role, type);
	    return Response.ok(users).build();
	}
	
	@PUT
	@Path("/deacUser/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deactivateUser(@PathParam("userId") String userId) {
		UserDAO dao=(UserDAO) ctx.getAttribute("userDAO");
		User user=dao.findUser(userId);
		user.setActivity(ActivityStatus.DEACTIVATED);
		dao.updateUser(userId, user);
	}
	



}
