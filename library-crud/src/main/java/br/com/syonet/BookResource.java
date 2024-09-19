package br.com.syonet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.quarkus.panache.common.Page;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/book")
public class BookResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> list(
        @QueryParam("size") Integer size, 
        @QueryParam("index") Integer index,  
        @QueryParam("year") Integer year, 
        @QueryParam("author") String author, 
        @QueryParam("title") String title,
        @QueryParam("email") String email) {

        if (index == null) index = 0;
        if (size == null) size = 2;

        String query = "1 = 1";
        Map<String, Object> params = new HashMap<>();

        if (email != null && !email.isEmpty()) {
            query += " AND email = :email";
            params.put("email", email);
        }
        if (title != null && !title.isEmpty()) {
            query += " AND title = :title";
            params.put("title", title);
        }
        if (year != null) {
            query += " AND year = :year";
            params.put("year", year);
        }

        return Book.find(query, params).page(Page.of(index, size)).list();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book get(@PathParam("id") Long id) {
        return Book.findById(id);
    }

    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Book create(Book book) {
        book.persist();
        return book;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Book update(@PathParam("id") Long id, Book book) {
        Book existingBook = Book.findById(id);
        if (existingBook != null) {
            existingBook.title = book.title;
            existingBook.author = book.author;
            existingBook.email = book.email;
            existingBook.year = book.year;
            existingBook.persist();
            return existingBook;
        }
        return null;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        Book book = Book.findById(id);
        if (book != null) {
            book.delete();
        }
    }
}
