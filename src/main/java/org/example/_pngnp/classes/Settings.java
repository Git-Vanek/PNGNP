// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.classes;

// Импорт классов для работы с настройками
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class Settings {
    private String language;
    private String themePath;

    // Геттеры и сеттеры
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getThemePath() {
        return themePath;
    }

    public void setThemePath(String themePath) {
        this.themePath = themePath;
    }

    // Метод для сохранения настроек в файл
    public static void saveSettings(Settings settings, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(filePath), settings);
    }

    // Метод для загрузки настроек из файла
    public static Settings loadSettings(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            // Создание файла с дефолтными настройками
            Settings defaultSettings = new Settings();
            defaultSettings.setLanguage("english");
            defaultSettings.setThemePath("/org/example/_pngnp/styles/dark-theme.css");
            saveSettings(defaultSettings, filePath);
            return defaultSettings;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, Settings.class);
    }
}