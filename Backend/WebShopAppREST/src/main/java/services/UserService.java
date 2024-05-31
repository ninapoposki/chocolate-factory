package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.User;
import dao.UserDAO;
import dto.RegisteredUserDTO;

import enumerations.Role;
import validations.PasswordValidation;
import validations.PersonalDataValidation;


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
	
	@POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(RegisteredUserDTO dto) {
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
	

}
