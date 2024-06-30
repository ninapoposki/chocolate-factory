package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import beans.Comment;
import enumerations.Role;

public class CommentDAO {

    private HashMap<String, Comment> comments = new HashMap<String, Comment>();
    private String contextPath;
    private AtomicInteger maxId = new AtomicInteger(0); // Dodato za praćenje maksimalnog ID-a

    public CommentDAO() {
    }

    public CommentDAO(String contextPath) {
        this.contextPath = contextPath;
        loadComments(contextPath);
    }

    public Collection<Comment> findAll() {
        return comments.values();
    }

    public Comment findComment(String commentId) {
        return comments.containsKey(commentId) ? comments.get(commentId) : null;
    }

    public Comment updateComment(String id, Comment comment) {
        Comment c = comments.containsKey(id) ? comments.get(id) : null;
        if (c == null) {
            return save(comment);
        } else {
            c.setFactoryId(comment.getFactoryId());
            c.setGrade(comment.getGrade());
            c.setText(comment.getText());
            c.setUserId(comment.getUserId());
            c.setRole(comment.getRole());
            saveComments();
        }
        return c;
    }

    public Comment save(Comment comment) {
        // Generisanje novog jedinstvenog ID-a
        String newId = String.valueOf(maxId.incrementAndGet());
        comment.setId(newId);
        comments.put(comment.getId(), comment);
        saveToFile(comment);
        return comment;
    }

    private void saveComments() {
        try {
            Path filePath = Paths.get(contextPath + "/comments.csv");
            try (BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), false))) {
                for (Comment comment : comments.values()) {
                    out.write(commentToCsv(comment) + "\n");
                }
                out.flush();
                System.out.println("All comments saved to file successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String commentToCsv(Comment comment) {
        return comment.getId() + "," +
               comment.getText() + "," +
               comment.getGrade() + "," +
               comment.getFactoryId() + "," +
               comment.getUserId() + "," +
               comment.getRole().toString();
    }

    private void saveToFile(Comment comment) {
        Path filePath = Paths.get(contextPath + "/comments.csv");
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filePath.toString(), true))) {
            out.write(commentToCsv(comment) + "\n"); // Dodato \n za novi red
            out.flush();
            System.out.println("Comment saved to file successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadComments(String contextPath) {
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/comments.csv");
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] data = line.split(",");
                if (data.length < 6) continue;

                String id = data[0].trim();
                String text = data[1].trim();
                double grade = Double.parseDouble(data[2].trim());
                int factoryId = Integer.parseInt(data[3].trim());
                int userId = Integer.parseInt(data[4].trim());
                Role role = Role.valueOf(data[5].trim().toUpperCase());

                Comment comment = new Comment(id, text, grade, factoryId, userId, role);
                comments.put(id, comment);

                // Ažuriranje maksimalnog ID-a
                int currentId = Integer.parseInt(id);
                if (currentId > maxId.get()) {
                    maxId.set(currentId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
