import java.io.*;
import java.util.*;

// ---------------------- Book Class -----------------------
class Book implements Comparable<Book> {

    int bookId;
    String title;
    String author;
    String category;
    boolean isIssued;

    public Book(int bookId, String title, String author, String category, boolean isIssued) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isIssued = isIssued;
    }

    public void displayBookDetails() {
        System.out.println("ID: " + bookId);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Category: " + category);
        System.out.println("Issued: " + (isIssued ? "Yes" : "No"));
    }

    public void markAsIssued() {
        isIssued = true;
    }

    public void markAsReturned() {
        isIssued = false;
    }

    @Override
    public int compareTo(Book b) {
        return this.title.compareToIgnoreCase(b.title);
    }
}

// Comparator for sorting by author
class AuthorComparator implements Comparator<Book> {
    public int compare(Book b1, Book b2) {
        return b1.author.compareToIgnoreCase(b2.author);
    }
}


// ---------------------- Member Class -----------------------
class Member {

    int memberId;
    String name;
    String email;
    List<Integer> issuedBooks = new ArrayList<>();

    public Member(int memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
    }

    public void addIssuedBook(int bookId) {
        issuedBooks.add(bookId);
    }

    public void returnIssuedBook(int bookId) {
        issuedBooks.remove(Integer.valueOf(bookId));
    }

    public void displayMemberDetails() {
        System.out.println("Member ID: " + memberId);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Issued Books: " + issuedBooks);
    }
}


// ---------------------- Library Manager -----------------------
class LibraryManager {

    Map<Integer, Book> books = new HashMap<>();
    Map<Integer, Member> members = new HashMap<>();

    Scanner sc = new Scanner(System.in);

    // ---------------------- File I/O -------------------------
    public void loadFromFile() {
        try {
            File file = new File("books.txt");
            if (!file.exists()) file.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader("books.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String arr[] = line.split(",");
                int id = Integer.parseInt(arr[0]);
                books.put(id, new Book(id, arr[1], arr[2], arr[3], Boolean.parseBoolean(arr[4])));
            }
            br.close();

            file = new File("members.txt");
            if (!file.exists()) file.createNewFile();

            br = new BufferedReader(new FileReader("members.txt"));
            while ((line = br.readLine()) != null) {
                String arr[] = line.split(",");
                int id = Integer.parseInt(arr[0]);

                Member m = new Member(id, arr[1], arr[2]);

                if (arr.length > 3 && !arr[3].equals("none")) {
                    String[] issued = arr[3].split(";");
                    for (String s : issued) m.addIssuedBook(Integer.parseInt(s));
                }

                members.put(id, m);
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Error loading file: " + e);
        }
    }

    public void saveToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("books.txt"));

            for (Book b : books.values()) {
                bw.write(b.bookId + "," + b.title + "," + b.author + "," + b.category + "," + b.isIssued);
                bw.newLine();
            }

            bw.close();

            bw = new BufferedWriter(new FileWriter("members.txt"));

            for (Member m : members.values()) {
                StringBuilder issued = new StringBuilder();

                for (int id : m.issuedBooks) {
                    issued.append(id).append(";");
                }

                if (issued.length() == 0) issued.append("none");

                bw.write(m.memberId + "," + m.name + "," + m.email + "," + issued);
                bw.newLine();
            }

            bw.close();

        } catch (Exception e) {
            System.out.println("Error saving file: " + e);
        }
    }


    // ---------------------- Operations -------------------------

    public void addBook() {
        System.out.print("Enter book title: ");
        sc.nextLine();
        String title = sc.nextLine();

        System.out.print("Enter author: ");
        String author = sc.nextLine();

        System.out.print("Enter category: ");
        String category = sc.nextLine();

        int id = 100 + books.size() + 1;

        Book b = new Book(id, title, author, category, false);
        books.put(id, b);

        saveToFile();
        System.out.println("Book added successfully with ID: " + id);
    }

    public void addMember() {
        sc.nextLine();
        System.out.print("Enter member name: ");
        String name = sc.nextLine();

        System.out.print("Enter email: ");
        String email = sc.nextLine();

        int id = 1 + members.size();

        Member m = new Member(id, name, email);
        members.put(id, m);
        saveToFile();

        System.out.println("Member added with ID: " + id);
    }

    public void issueBook() {
        System.out.print("Enter book ID: ");
        int bid = sc.nextInt();

        System.out.print("Enter member ID: ");
        int mid = sc.nextInt();

        if (!books.containsKey(bid)) {
            System.out.println("Book not found!");
            return;
        }

        if (!members.containsKey(mid)) {
            System.out.println("Member not found!");
            return;
        }

        Book b = books.get(bid);

        if (b.isIssued) {
            System.out.println("Book already issued.");
            return;
        }

        b.markAsIssued();
        members.get(mid).addIssuedBook(bid);

        saveToFile();
        System.out.println("Book issued successfully.");
    }

    public void returnBook() {
        System.out.print("Enter book ID: ");
        int bid = sc.nextInt();

        if (!books.containsKey(bid)) {
            System.out.println("Book not found!");
            return;
        }

        books.get(bid).markAsReturned();

        for (Member m : members.values()) {
            m.returnIssuedBook(bid);
        }

        saveToFile();
        System.out.println("Book returned.");
    }

    public void searchBooks() {
        sc.nextLine();
        System.out.print("Enter keyword: ");
        String key = sc.nextLine().toLowerCase();

        for (Book b : books.values()) {
            if (b.title.toLowerCase().contains(key) ||
                b.author.toLowerCase().contains(key) ||
                b.category.toLowerCase().contains(key)) {

                b.displayBookDetails();
                System.out.println("------------------");
            }
        }
    }

    public void sortBooks() {
        List<Book> list = new ArrayList<>(books.values());

        System.out.println("1. Sort by Title");
        System.out.println("2. Sort by Author");

        int ch = sc.nextInt();

        if (ch == 1) Collections.sort(list);
        else Collections.sort(list, new AuthorComparator());

        for (Book b : list) {
            b.displayBookDetails();
            System.out.println();
        }
    }

    public void menu() {

        loadFromFile();

        int ch = 0;

        while (ch != 7) {
            System.out.println("\n--- City Library Digital Management System ---");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Books");
            System.out.println("6. Sort Books");
            System.out.println("7. Exit");

            System.out.print("Enter your choice: ");
            ch = sc.nextInt();

            switch (ch) {
                case 1: addBook(); break;
                case 2: addMember(); break;
                case 3: issueBook(); break;
                case 4: returnBook(); break;
                case 5: searchBooks(); break;
                case 6: sortBooks(); break;
                case 7:
                    saveToFile();
                    System.out.println("Exiting..."); break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}


// ---------------------- MAIN APP -----------------------
public class CityLibraryApp {
    public static void main(String[] args) {
        LibraryManager lm = new LibraryManager();
        lm.menu();
    }
}
