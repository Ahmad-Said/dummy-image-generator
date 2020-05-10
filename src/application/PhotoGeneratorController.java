package application;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import application.fxGraphics.IntField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;

public class PhotoGeneratorController implements Initializable {

	@FXML
	private IntField numberOfImage;

	@FXML
	private IntField widthImage;
	@FXML
	private IntField heightImage;
	@FXML
	private IntField randomSizeWH;
	@FXML
	private IntField shapesFillNumber;

	@FXML
	private IntField lengthName;
	@FXML
	private TextField charsetName;

	@FXML
	private TextFlow targetDirectoryFlow;
	@FXML
	private Text logText;
	@FXML
	private Text targetDirectoryText;

	@FXML
	private SplitMenuButton generateButton;

	private List<Color> painterColorCollection;

	private Path targetDirectory;
	private Boolean isGenerating = true;
	private static final String LAST_DIRECTORY_PREF_KEY = "LAST_DIRECTORY";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		charsetName.setText(NameGenerator.getLexicon());
		logText.setText("");
		setTargetDirectory(getInitialDirectory().toPath());
		ToggleGroup allThreadMenuItems = new ToggleGroup();
		for (int i = 1; i <= 8; i++) {
			RadioMenuItem mn = new RadioMenuItem(i + " Threads");
			generateButton.getItems().add(mn);
			int threadNb = i;
			mn.setOnAction(e -> {
				executors.shutdown();
				executors = Executors.newFixedThreadPool(threadNb);
			});
			allThreadMenuItems.getToggles().add(mn);
		}
		executors = Executors.newFixedThreadPool(2);
		allThreadMenuItems.getToggles().get(1).setSelected(true);
	}

	public PhotoGeneratorController() {
		painterColorCollection = new ArrayList<Color>();
		// take too long time
//		for (int r = 0; r <= 255; r++) {
//			for (int g = 0; g <= 255; g++) {
//				for (int b = 0; b <= 255; b++) {
//					painterColorCollection.add(new Color(r, g, b));
//				}
//			}
//		}
		// add your color here
		painterColorCollection.add(Color.BLACK);
		painterColorCollection.add(Color.BLUE);
		painterColorCollection.add(Color.CYAN);
		painterColorCollection.add(Color.DARK_GRAY);
		painterColorCollection.add(Color.GREEN);
		painterColorCollection.add(Color.MAGENTA);
		painterColorCollection.add(Color.ORANGE);
		painterColorCollection.add(Color.PINK);
		painterColorCollection.add(Color.WHITE);
		painterColorCollection.add(Color.RED);
		painterColorCollection.add(Color.YELLOW);
	}

	private void setTargetDirectory(Path dir) {
		targetDirectory = dir;
		targetDirectoryText.setText("\n" + dir.toString());
	}

	private File getInitialDirectory() {
		// initial directory
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		String initialDir = prefs.get(LAST_DIRECTORY_PREF_KEY, null);
		File initialFile = new File(initialDir);
		if (!initialFile.exists()) {
			initialFile = new File(System.getProperty("user.home"));
		}
		return initialFile;
	}

	@FXML
	private void browseTargetDirectory() {

		// https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Navigate where to generate images");

		directoryChooser.setInitialDirectory(getInitialDirectory());
		File toGenerateIn = directoryChooser.showDialog(Main.getPrimaryStage());
		if (toGenerateIn == null) {
			return;
		}
		setTargetDirectory(toGenerateIn.toPath());
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		prefs.put(LAST_DIRECTORY_PREF_KEY, targetDirectory.toString());
	}

	@FXML
	private void openTargetDirectory() {
		try {
			Desktop.getDesktop().open(getInitialDirectory());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	ExecutorService executors;

	@FXML
	private void generatePhoto() {
		if (targetDirectory == null || !targetDirectory.toFile().exists()) {
			logText.setText("Location Cannot be found !!... Browse a new location");
			browseTargetDirectory();
			if (targetDirectory == null) {
				return;
			}
		}
		if (charsetName.getText().isEmpty()) {
			logText.setText("Charset Cannot be empty.. we put an example text for you");
			charsetName.setText(NameGenerator.getAlphabetLexicon());
			return;
		} else {
			NameGenerator.setLexicon(charsetName.getText());
		}
		NameGenerator.setLength(lengthName.getValue());
		logText.setText("");
		isGenerating = true;
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfImage.getValue(); i++) {
			int imageIndex = i + 1;
			if (!isGenerating) {
				return;
			}
			executors.execute(() -> {
				if (!isGenerating) {
					return;
				}
				File targetFile = targetDirectory.resolve(NameGenerator.getRandomIdentifier() + ".jpg").toFile();
				int randomWidth = randomSizeWH.getValue() == 0 ? 0 : rand.nextInt(randomSizeWH.getValue());
				int randomHeight = randomSizeWH.getValue() == 0 ? 0 : rand.nextInt(randomSizeWH.getValue());
				convertImageToFile(generateRandomImage(widthImage.getValue() + randomWidth,
						heightImage.getValue() + randomHeight, shapesFillNumber.getValue()), targetFile);
				long endTime = System.currentTimeMillis() - startTime;
				Platform.runLater(() -> logText.setText("Took about " + endTime / 1000.0 + " Seconds.\nGenerating ..."
						+ imageIndex + " / " + numberOfImage.getValue() + "\nName: " + targetFile.getName()));
			});
		}
	}

	@FXML
	private void stopGenerating() {
		isGenerating = false;
	}

	private final static java.util.Random rand = new java.util.Random();

	private BufferedImage generateRandomImage(int width, int height, int numberOfShapes) {
		BufferedImage imgb1;
		imgb1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = imgb1.createGraphics();
//		// Choose painter color
//		graphics.setPaint(painterColorCollection.get(rand.nextInt(painterColorCollection.size())));
//		// Fill background
//		graphics.fillRect(0, 0, imgb1.getWidth(), imgb1.getHeight());

		// random shapes
		int shapeWidth = width / numberOfShapes;
		int shapeHeight = height / numberOfShapes;
		for (int i = 0; i < numberOfShapes; i++) {
			for (int j = 0; j < numberOfShapes; j++) {
				int randomNum = rand.nextInt(painterColorCollection.size());
				graphics.setPaint(painterColorCollection.get(randomNum));
				if (randomNum % 2 == 0) {
					graphics.fillRect(i * shapeWidth, j * shapeHeight, shapeWidth, shapeHeight);
				} else {
					graphics.fillOval(i * shapeWidth, j * shapeHeight, shapeWidth, shapeHeight);
				}
			}
		}

		graphics.dispose();
		return imgb1;
	}

	private File convertImageToFile(BufferedImage bImage, File outputFile) {
		try {
			ImageIO.write(bImage, "jpg", outputFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return outputFile;
	}

}
