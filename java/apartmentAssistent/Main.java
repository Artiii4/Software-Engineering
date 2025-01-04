package apartmentAssistent;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        SwingUtilities.invokeLater(() -> {
            Application app = null;
            try {
                app = new Application();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            app.setVisible(true);
        });
    }
}
