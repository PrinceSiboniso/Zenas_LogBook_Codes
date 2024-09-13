package org.example.xmlprocessor;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Controller {

    public static void editBook(int bookIndex, String title, String author, String publisher, int publicationYear) {
        System.out.println("Editing book...");
        try {
            File xmlFile = new File("src/main/resources/org/example/xmlprocessor/books.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            NodeList bookNodes = doc.getElementsByTagName("book");
            if (bookIndex >= 0 && bookIndex < bookNodes.getLength()) {
                Node bookNode = bookNodes.item(bookIndex);
                System.out.println("Current node: " + bookNode.getNodeName());

                // Clear existing content
                while (bookNode.hasChildNodes()) {
                    bookNode.removeChild(bookNode.getFirstChild());
                }

                // Add new book details
                Element titleElement = doc.createElement("title");
                titleElement.appendChild(doc.createTextNode(title));
                bookNode.appendChild(titleElement);

                Element authorElement = doc.createElement("author");
                authorElement.appendChild(doc.createTextNode(author));
                bookNode.appendChild(authorElement);

                Element publisherElement = doc.createElement("publisher");
                publisherElement.appendChild(doc.createTextNode(publisher));
                bookNode.appendChild(publisherElement);

                Element publicationYearElement = doc.createElement("publication_year");
                publicationYearElement.appendChild(doc.createTextNode(String.valueOf(publicationYear)));
                bookNode.appendChild(publicationYearElement);

                Element descriptionElement = doc.createElement("description");
                bookNode.appendChild(descriptionElement); // Assuming description will be added later

                // Save changes back to the XML file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.transform(new DOMSource(doc), new StreamResult(new File("src/main/resources/org/example/xmlprocessor/books.xml")));

                System.out.println("Book edited successfully.");
            } else {
                System.out.println("Invalid book index.");
            }
        } catch (Exception e) {
            System.out.println("XML Error: " + e.getMessage());
        }
    }

    public static Book[] retrieveBooks() {
        try {
            File xmlFile = new File("src/main/resources/org/example/xmlprocessor/books.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList bookNodes = doc.getElementsByTagName("book");
            Book[] booksArray = new Book[bookNodes.getLength()];

            for (int i = 0; i < bookNodes.getLength(); i++) {
                Element bookElement = (Element) bookNodes.item(i);
                if (bookElement != null && bookElement.getNodeType() == Node.ELEMENT_NODE) {
                    String title = bookElement.getElementsByTagName("title").item(0).getTextContent();
                    String author = bookElement.getElementsByTagName("author").item(0).getTextContent();
                    String publisher = bookElement.getElementsByTagName("publisher").item(0).getTextContent();
                    int publicationYear = Integer.parseInt(bookElement.getElementsByTagName("publication_year").item(0).getTextContent());
                    String description = bookElement.getElementsByTagName("description").item(0).getTextContent();

                    booksArray[i] = new Book(title, author, publisher, publicationYear, description);
                }
            }
            return booksArray;
        } catch (Exception e) {
            System.err.println("Error while reading books: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void displayBookList() {
        Book[] books = retrieveBooks();

        System.out.println("\nYou have " + books.length + " books. See below:");
        System.out.println("---------------------------------------------------------\n");

        for (int i = 0; i < books.length; i++) {
            System.out.println("Book " + (i + 1));
            System.out.println("Title: " + books[i].getTitle());
            System.out.println("Author: " + books[i].getAuthor());
            System.out.println("Publisher: " + books[i].getPublisher());
            System.out.println("Published Year: " + books[i].getPublicationYear());
            System.out.println("\n");
        }
    }

    public static Book[] searchBooks(String keyword) {
        Book[] allBooks = retrieveBooks();
        ArrayList<Book> matchingBooks = new ArrayList<>();
        String searchKey = keyword.toLowerCase();

        for (Book book : allBooks) {
            if (book.getAuthor().toLowerCase().contains(searchKey) ||
                book.getDescription().toLowerCase().contains(searchKey) ||
                book.getTitle().toLowerCase().contains(searchKey) ||
                book.getPublisher().toLowerCase().contains(searchKey)) {
                matchingBooks.add(book);
            }
        }

        if (matchingBooks.isEmpty()) {
            System.out.println("No matches found.");
            return new Book[0]; // Return an empty array if no matches are found
        }
        return matchingBooks.toArray(new Book[0]);
    }

    public static void addBook(String title, String author, String publisher, int publicationYear) {
        try {
            File xmlFile = new File("src/main/resources/org/example/xmlprocessor/books.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            // Create new book element
            Element newBookElement = doc.createElement("book");

            // Create and append child elements for the new book
            Element titleElement = doc.createElement("title");
            titleElement.appendChild(doc.createTextNode(title));
            newBookElement.appendChild(titleElement);

            Element authorElement = doc.createElement("author");
            authorElement.appendChild(doc.createTextNode(author));
            newBookElement.appendChild(authorElement);

            Element publisherElement = doc.createElement("publisher");
            publisherElement.appendChild(doc.createTextNode(publisher));
            newBookElement.appendChild(publisherElement);

            Element publicationYearElement = doc.createElement("publication_year");
            publicationYearElement.appendChild(doc.createTextNode(String.valueOf(publicationYear)));
            newBookElement.appendChild(publicationYearElement);

            Element descriptionElement = doc.createElement("description");
            newBookElement.appendChild(descriptionElement);

            // Add the new book element to the root element
            doc.getDocumentElement().appendChild(newBookElement);

            // Write the updated XML document back to the file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

            System.out.println("New book added successfully!");
        } catch (Exception e) {
            System.err.println("Failed to add new book: " + e.getMessage());
        }
    }

    public static void deleteBook(int bookIndex) {
        try {
            File xmlFile = new File("src/main/resources/org/example/xmlprocessor/books.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            NodeList bookNodes = doc.getElementsByTagName("book");
            if (bookIndex >= 0 && bookIndex < bookNodes.getLength()) {
                Node bookNode = bookNodes.item(bookIndex);
                System.out.println("Current node: " + bookNode.getNodeName());

                // Remove all child nodes
                while (bookNode.hasChildNodes()) {
                    bookNode.removeChild(bookNode.getFirstChild());
                }

                // Save changes back to the XML file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.transform(new DOMSource(doc), new StreamResult(new File("src/main/resources/org/example/xmlprocessor/books.xml")));

                System.out.println("Book deleted successfully.");
            } else {
                System.out.println("Invalid book index.");
            }
        } catch (Exception e) {
            System.out.println("XML Error: " + e.getMessage());
        }
    }
}
