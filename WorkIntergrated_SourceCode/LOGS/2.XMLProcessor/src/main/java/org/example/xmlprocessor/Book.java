package org.example.xmlprocessor;

public class Book 
{
    // Fields to store book information
    private String title;
    private String author;
    private String publisher;
    private int publicationYear;
    private String description;

    // Constructor for creating a Book object with title, author, publisher, and publicationYear
    public Book(String title, String author, String publisher, int publicationYear) 
    {
        this(title, author, publisher, publicationYear, "");
    }

    // Constructor for creating a Book object with title, author, publisher, publicationYear, and description
    public Book(String title, String author, String publisher, int publicationYear, String description) 
    {
        if (title == null || title.isEmpty()) 
        {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        if (author == null || author.isEmpty()) 
        {
            throw new IllegalArgumentException("Author cannot be null or empty.");
        }
        if (publisher == null || publisher.isEmpty()) 
        {
            throw new IllegalArgumentException("Publisher cannot be null or empty.");
        }
        if (publicationYear <= 0) 
        {
            throw new IllegalArgumentException("Publication year must be a positive integer.");
        }

        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.description = description != null ? description : "";
    }

    // Getter methods for accessing private fields
    public String getTitle() 
    {
        return title;
    }

    public String getAuthor() 
    {
        return author;
    }

    public String getPublisher() 
    {
        return publisher;
    }

    public int getPublicationYear() 
    {
        return publicationYear;
    }

    public String getDescription() 
    {
        return description;
    }

    // Setter methods for updating private fields
    public void setTitle(String title) 
    {
        if (title == null || title.isEmpty()) 
        {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        this.title = title;
    }

    public void setAuthor(String author) 
    {
        if (author == null || author.isEmpty()) 
        {
            throw new IllegalArgumentException("Author cannot be null or empty.");
        }
        this.author = author;
    }

    public void setPublisher(String publisher) 
    {
        if (publisher == null || publisher.isEmpty()) 
        {
            throw new IllegalArgumentException("Publisher cannot be null or empty.");
        }
        this.publisher = publisher;
    }

    public void setPublicationYear(int publicationYear) 
    {
        if (publicationYear <= 0) 
        {
            throw new IllegalArgumentException("Publication year must be a positive integer.");
        }
        this.publicationYear = publicationYear;
    }

    public void setDescription(String description) 
    {
        this.description = description != null ? description : "";
    }

    // Overriding toString method to return book details
    @Override
    public String toString() 
    {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publicationYear=" + publicationYear +
                ", description='" + description + '\'' +
                '}';
    }

    // Overriding equals method to compare Book objects
    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (publicationYear != book.publicationYear) return false;
        if (!title.equals(book.title)) return false;
        if (!author.equals(book.author)) return false;
        if (!publisher.equals(book.publisher)) return false;
        return description.equals(book.description);
    }

    // Overriding hashCode method to generate hash based on Book properties
    @Override
    public int hashCode() 
    {
        int result = title.hashCode();
        result = 31 * result + author.hashCode();
        result = 31 * result + publisher.hashCode();
        result = 31 * result + publicationYear;
        result = 31 * result + description.hashCode();
        return result;
    }
}
