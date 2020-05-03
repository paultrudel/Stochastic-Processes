package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.CheckListView;

import annealing.AnnealingModel;
import annealing.City;
import annealing.Cost;
import annealing.Tour;
import ising.Configuration;
import ising.Estimate;
import ising.IsingModel;
import ising.IsingModel.Spin;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import queueing.MM1Queue;
import queueing.MM1Queue.QueueType;
import queueing.Time;

public class Interface extends Application {

	private static final int MARGIN = 20;
	public static final int FIT = 1200;
	public static final int MAP_WIDTH = FIT;
	public static final int MAP_HEIGHT = (int)(FIT / 1.42);
	public static final int MAX_DISTANCE = (int) Math.sqrt(Math.pow(MAP_WIDTH, 2) + 
			Math.pow(MAP_HEIGHT, 2));
	
	private Scene scene;
	private Stage primaryStage;
	private BorderPane root;
	private Bounds bounds;
	private Group map;
	private Group citiesGroup;
	private ToggleGroup tourGroup;
	private Group lattice;
	private Group sites;
	private AnnealingModel annealingModel;
	private IsingModel isingModel;
	private MM1Queue queue;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		annealingModel = new AnnealingModel(this);
		isingModel = new IsingModel(this);
		queue = new MM1Queue(this);
		drawMenu();
	}
	
	private void drawMenu() {
		root = new BorderPane();
		root.setPadding(new Insets(10, 20, 10, 20));
		drawTitle();
		drawOptions();
		setScene();
		setStage();
	}
	
	private void drawTitle() {
		VBox heading = new VBox(10);
		Label title = new Label("STAT 3506 Final Project Winter 2020");
		title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
		Label author = new Label("Paul Trudel 100940324");
		author.setStyle("-fx-font-size: 20px;");
		heading.getChildren().addAll(title, author);
		heading.setAlignment(Pos.CENTER);
		root.setTop(heading);
		BorderPane.setAlignment(heading, Pos.CENTER);
	}
	
	private void drawOptions() {
		VBox optionPane = new VBox(50);
		
		Button simulatedAnnealing = new Button("Simulated Annealing");
		simulatedAnnealing.setOnAction(e -> {
			drawAnnealing();
		});
		
		Button isingModel = new Button("Ising Model");
		isingModel.setOnAction(e -> {
			drawIsing();
		});
		
		Button queueing = new Button("Queueing");
		queueing.setOnAction(e -> {
			drawQueueing();
		});
		
		optionPane.getChildren().addAll(simulatedAnnealing, isingModel, queueing);
		optionPane.setAlignment(Pos.CENTER);
		root.setCenter(optionPane);
		BorderPane.setAlignment(optionPane, Pos.CENTER);
	}
	
	private void drawAnnealing() {
		root = new BorderPane();
		root.setPadding(new Insets(10, 20, 10, 20));
		drawAnnealingTop();
		drawAnnealingMap();
		drawAnnealingButtons();
		setScene();
		setStage();
	}
	
	private void drawAnnealingTop() {
		HBox top = new HBox(650);
		
		Button back = new Button("Back");
		back.setOnAction(e -> {
			drawMenu();
		});
		
		Label title = new Label("Travelling Salesman Problem Using Simulated Annealing");
		title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		
		top.getChildren().addAll(back, title);
		root.setTop(top);
	}
	
	private void drawAnnealingMap() {
		Image img = new Image("file:images/map.jpg");
		ImageView imgView = new ImageView(img);
		imgView.setFitHeight(FIT);
		imgView.setFitWidth(FIT);
		imgView.setPreserveRatio(true);
		map = new Group(imgView);
		root.setCenter(map);
		drawLegend();
	}
	
	private void drawLegend() {
		GridPane legend = new GridPane();
		legend.setHgap(10);
		legend.setVgap(10);
		legend.setPadding(new Insets(10, 10, 10, 10));
		
		int rowNum = 0;
		Label title = new Label("Legend");
		title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		legend.add(title, 0, rowNum++);
		
		Circle starting = new Circle(5);
		starting.setFill(Color.BLUE);
		starting.setStroke(Color.BLACK);
		legend.add(starting, 0, rowNum);
		legend.add(new Label("Starting City"), 1, rowNum++);
		
		Circle notVisited = new Circle(5);
		notVisited.setFill(Color.RED);
		notVisited.setStroke(Color.BLACK);
		legend.add(notVisited, 0, rowNum);
		legend.add(new Label("City Not Visited"), 1, rowNum++);
		
		Circle visited = new Circle(5);
		visited.setFill(Color.GREEN);
		visited.setStroke(Color.BLACK);
		legend.add(visited, 0, rowNum);
		legend.add(new Label("City Visited"), 1, rowNum++);
		
		drawTourGroup(legend, rowNum);
		root.setRight(legend);
	}
	
	private int drawTourGroup(GridPane grid, int rowNum) {
		rowNum += 5;
		tourGroup = new ToggleGroup();
		
		RadioButton initialTour = new RadioButton("Initial Tour");
		initialTour.setToggleGroup(tourGroup);
		grid.add(initialTour, 0, rowNum++);
		
		RadioButton finalTour = new RadioButton("Final Tour");
		finalTour.setToggleGroup(tourGroup);
		grid.add(finalTour, 0, rowNum++);
		
		tourGroup.selectToggle(finalTour);
		setTourToggleDisable(true);
		
		tourGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, 
					Toggle newValue) {
				RadioButton rb = (RadioButton) tourGroup.getSelectedToggle();
				if(rb != null) {
					String text = rb.getText();
					if(text.equals("Initial Tour")) {
						Tour tour = annealingModel.getTour(0);
						drawTour(tour);
					}
					else {
						Tour tour = annealingModel.getTour(1);
						drawTour(tour);
					}
						
				}
			}
		});
		
		return rowNum;
	}
	
	public void setTourToggleDisable(boolean disable) {
		for(Toggle rb: tourGroup.getToggles())
			((RadioButton) rb).setDisable(disable);
	}
	
	private void drawAnnealingButtons() {
		HBox buttonPane = new HBox(50);
		
		Button settings = new Button("Settings");
		settings.setOnAction(e -> {
			annealingSettingsDialog();
		});
		
		Button viewCities = new Button("View Cities");
		viewCities.setOnAction(e -> {
			drawCities();
			viewCitiesDialog();
		});
		
		Button viewCosts = new Button("View Costs");
		viewCosts.setOnAction(e -> {
			viewCostsDialog();
		});
		
		Button createTour = new Button("Create Tour");
		createTour.setOnAction(e -> {
			annealingModel.createTour();
		});
		
		Button tourSummary = new Button("Tour Summary");
		tourSummary.setOnAction(e -> {
			tourSummaryDialog();
		});
		
		Button reset = new Button("Reset");
		reset.setOnAction(e -> {
			annealingModel.resetCities();
			drawCities();
		});
		
		buttonPane.getChildren().addAll(settings, viewCities, viewCosts, createTour, 
				tourSummary, reset);
		buttonPane.setAlignment(Pos.CENTER);
		root.setBottom(buttonPane);
		BorderPane.setAlignment(buttonPane, Pos.CENTER);
	}
	
	private void annealingSettingsDialog() {
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("Travelling Salesman Settings");
		
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		int rowNum = 0;
		rowNum = citySettings(grid, rowNum);
		rowNum = algorithmSettings(grid,rowNum);
		
		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();
	}
	
	private int citySettings(GridPane grid, int rowNum) {
		rowNum = createCitySettings(grid, rowNum);
		return rowNum;
	}
	
	private int createCitySettings(GridPane grid, int rowNum) {
		Label citySettings = new Label("City Settings");
		citySettings.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		grid.add(citySettings, 0, rowNum++);
		grid.add(new Label("Create a random set of cities"), 0, rowNum++);
		TextField numCities = new TextField();
		numCities.setPromptText("Between 10 and 100");
		Button createCities = new Button("Create Random Cities");
		createCities.setOnAction(e -> {
			try {
				int num = Integer.parseInt(numCities.getText());
				if(num > 0)
					annealingModel.createCities(num);
				else
					showAlert("City Number Error", "The number of cities must be greated than zero");					
			} catch(NumberFormatException exception) {
				showAlert("City Number Error", "The number of cities must be a number (obviously)");
			}
		});
		
		Button resetCities = new Button("Reset Cities");
		resetCities.setOnAction(e -> {
			annealingModel.resetCities();
			drawCities();
		});
		
		grid.add(new Label("Number of cities"), 0, rowNum++);
		grid.add(numCities, 1, rowNum - 1);
		grid.add(createCities, 0, rowNum++);
		grid.add(resetCities, 1, rowNum - 1);
		
		rowNum+=2;
		grid.add(new Label("Add a city"), 0, rowNum++);
		TextField location = new TextField();
		location.setPromptText("0 < x < " + MAP_WIDTH + ", 0 < y < " + MAP_HEIGHT);
		
		Button addCity = new Button("Add City");
		addCity.setOnAction(e -> {
			String[] coords = location.getText().split(",");
			try {
				double x = Double.parseDouble(coords[0]);
				double y = Double.parseDouble(coords[1]);
				if((x > 0 && x < MAP_WIDTH) && (y > 0 && y < MAP_HEIGHT)) {
					Point2D point = new Point2D(x, y);
					annealingModel.addCity(point);
					showAlert("City Added", "City was successfully added");
				}
				else
					showAlert("City Location Error", "City location must have 0 < x < " + MAP_WIDTH + " and"
							+ " 0 < y < " + MAP_HEIGHT);
			} catch(NumberFormatException exception) {
				showAlert("City Location Error", "City location must have x and y both be numbers");
			}
		});
		
		grid.add(new Label("City location"), 0, rowNum++);
		grid.add(location, 1, rowNum - 1);
		grid.add(addCity, 0, rowNum++);
		return rowNum;
	}
	
	private int connectivitySettings(GridPane grid, int rowNum) {
		rowNum += 2;
		grid.add(new Label("City Connectivity"), 0, rowNum++);
		
		HBox connectivityType = new HBox(10);
		ToggleGroup connectivityTypes = new ToggleGroup();
		RadioButton fullyConnected = new RadioButton(AnnealingModel.Connection.FULL.getType());
		fullyConnected.setToggleGroup(connectivityTypes);
		RadioButton limitConnected = new RadioButton(AnnealingModel.Connection.LIMITED.getType());
		limitConnected.setToggleGroup(connectivityTypes);
		connectivityTypes.selectToggle(fullyConnected);
		connectivityType.getChildren().addAll(fullyConnected, limitConnected);
		grid.add(connectivityType, 0, rowNum++);
		
		HBox limitedConnOption = new HBox(10);
		ObservableList<String> options = FXCollections.observableArrayList("Limit by distance", 
				"Limit by number");
		CheckListView<String> limitedConnOptions = new CheckListView<String>();
		limitedConnOptions.getItems().addAll(options);
		limitedConnOptions.prefHeightProperty().bind(Bindings.size(options).multiply(30));
		if(connectivityTypes.getSelectedToggle().equals(fullyConnected))
			limitedConnOptions.setDisable(true);
		else
			limitedConnOptions.setDisable(false);
		limitedConnOption.getChildren().add(limitedConnOptions);
		grid.add(limitedConnOption, 0, 11);
		
		grid.add(new Label("Distance limit"), 0, 12);
		TextField distLimit = new TextField();
		if(limitedConnOptions.getSelectionModel().isSelected(0))
			distLimit.setDisable(false);
		else
			distLimit.setDisable(true);
		grid.add(distLimit, 1, 12);
		
		grid.add(new Label("Number limit"), 0, 13);
		TextField numLimit = new TextField();
		if(limitedConnOptions.getSelectionModel().isSelected(1))
			numLimit.setDisable(false);
		else
			numLimit.setDisable(true);
		grid.add(numLimit, 1, 13);
		
		connectivityTypes.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, 
					Toggle newValue) {
				RadioButton rb = (RadioButton) connectivityTypes.getSelectedToggle();
				if(rb != null) {
					String text = rb.getText();
					if(text.equals(fullyConnected.getText())) {
						limitedConnOptions.setDisable(true);
						distLimit.setDisable(true);
						numLimit.setDisable(true);
					}
					else
						limitedConnOptions.setDisable(false);
				}
			}
		});
		
		limitedConnOptions.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
			@Override
			public void onChanged(Change<? extends String> c) {
				if((limitedConnOptions.getCheckModel().getCheckedItems()).contains(options.get(0)))
					distLimit.setDisable(false);
				else
					distLimit.setDisable(true);
				if((limitedConnOptions.getCheckModel().getCheckedItems()).contains(options.get(1)))
					numLimit.setDisable(false);
				else
					numLimit.setDisable(true);
			}
		});
		
		Button setConnectivity = new Button("Create Connections");
		setConnectivity.setOnAction(e -> {
			RadioButton rb = (RadioButton) connectivityTypes.getSelectedToggle();
			String connectionType = rb.getText();
			if(connectionType.equals(AnnealingModel.Connection.FULL.getType()))
				annealingModel.createConnections(connectionType, 0, 0);
			else {
				try {
					int dLimit = Integer.parseInt(distLimit.getText());
					int nLimit = Integer.parseInt(numLimit.getText());
					annealingModel.createConnections(connectionType, dLimit, nLimit);
				} catch (NumberFormatException exception) {
					exception.printStackTrace();
				}
			}
		});
		grid.add(setConnectivity, 0, 14);
		return rowNum;
	}
	
	private int algorithmSettings(GridPane grid, int rowNum) {
		rowNum+=2;
		Label algorithmSettings = new Label("Algorithm Settings");
		algorithmSettings.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		grid.add(algorithmSettings, 0, rowNum++);
		
		grid.add(new Label("Number of iterations"), 0, rowNum++);
		TextField numIterations = new TextField();
		numIterations.setText(String.valueOf(annealingModel.getNumIterations()));
		grid.add(numIterations, 1, rowNum - 1);
		
		Button setIterations = new Button("Set Iterations");
		setIterations.setOnAction(e -> {
			try {
				int num = Integer.parseInt(numIterations.getText());
				if(num > 0)
					annealingModel.setNumIterations(num);
				else
					showAlert("Iteration Value Error", "The number of iterations must be greater than zero");
			} catch(NumberFormatException exception) {
				showAlert("Iteration Value Error", "The number of iterations must be a number (obviously)");
			}
		});
		grid.add(setIterations, 0, rowNum++);
		return rowNum;
	}
	
	private void viewCitiesDialog() {
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("Cities");
		dialog.setHeaderText("Current cities in the system");
		
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TableView<City> table = new TableView<City>();
		ObservableList<City> cities = FXCollections.observableArrayList(annealingModel.getCities().values());
		
		TableColumn<City, String> nameCol = new TableColumn<City, String>("City Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		TableColumn<City, String> locationCol = new TableColumn<City, String>("City Location");
		locationCol.setCellValueFactory(new PropertyValueFactory<>("locationProperty"));
		
		TableColumn<City, String> reachableCol = new TableColumn<City, String>("Directly Reachable Cities");
		reachableCol.setCellValueFactory(new PropertyValueFactory<>("reachableCitiesProperty"));
		
		table.setItems(cities);
		table.getColumns().addAll(nameCol, locationCol, reachableCol);
		
		grid.add(table, 0, 0);
		
		dialog.getDialogPane().setContent(grid);
		
		dialog.showAndWait();
	}
	
	private void viewCostsDialog() {
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("Costs");
		dialog.setHeaderText("The costs for travelling from one city to another");
		
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TableView<Cost> table = new TableView<Cost>();
		ObservableList<Cost> costs = FXCollections.observableArrayList(annealingModel.getCosts());
		
		TableColumn<Cost, String> cityACol = new TableColumn<Cost, String>("City A");
		cityACol.setCellValueFactory(new PropertyValueFactory<>("firstCity"));
		
		TableColumn<Cost, String> cityBCol = new TableColumn<Cost, String>("City B");
		cityBCol.setCellValueFactory(new PropertyValueFactory<>("secondCity"));
		
		TableColumn<Cost, String> costCol = new TableColumn<Cost, String>("Cost");
		costCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
		
		table.setItems(costs);
		table.getColumns().addAll(cityACol, cityBCol, costCol);
		
		grid.add(table, 0, 0);
		
		dialog.getDialogPane().setContent(grid);
		
		dialog.showAndWait();
	}
	
	private void tourSummaryDialog() {
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("Tour Summary");
		dialog.setHeaderText("Summary of the changes to the tours over time");
		
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(10, 20, 10, 20));
		drawTourTable(pane);
		drawTourPlot(pane);
		
		dialog.getDialogPane().setContent(pane);
		dialog.showAndWait();
	}
	
	private void drawTourTable(BorderPane pane) {
		TableView<Tour> table = new TableView<Tour>();
		ObservableList<Tour> tours = FXCollections.observableArrayList(annealingModel.getTours());
		
		TableColumn<Tour, String> nameCol = new TableColumn<Tour, String>("Tour Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		TableColumn<Tour, String> costCol = new TableColumn<Tour, String>("Cost");
		costCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
		
		TableColumn<Tour, String> cityOrderCol = new TableColumn<Tour, String>("City Visit Order");
		cityOrderCol.setCellValueFactory(new PropertyValueFactory<>("cities"));
		
		table.setItems(tours);
		table.getColumns().addAll(nameCol, costCol, cityOrderCol);
		table.prefHeightProperty().bind(Bindings.size(tours).multiply(55));
		
		pane.setTop(table);
	}
	
	private void drawTourPlot(BorderPane pane) {
		List<Double> epochs = new ArrayList<Double>();
		List<Double> utilities = new ArrayList<Double>();
		for(Map.Entry<Integer, Double> entry: annealingModel.getUtilities().entrySet()) {
			epochs.add((double) entry.getKey());
			utilities.add(entry.getValue());
		}
		
		double minUtility = Collections.min(utilities);
		double maxUtility = Collections.max(utilities);
		Plot plot = Plot.plot(Plot.plotOpts().title("Utilities vs Epochs"));
		plot.xAxis("Epochs", Plot.axisOpts().range(0, epochs.size()));
		plot.yAxis("Utility Values", Plot.axisOpts()
				.range(minUtility - (minUtility / 20.0), maxUtility + (maxUtility / 20.0)));
		plot.series("Data", Plot.data().xy(epochs, utilities), Plot.seriesOpts()
				.marker(Plot.Marker.DIAMOND)
				.markerColor(java.awt.Color.GREEN)
				.color(java.awt.Color.BLACK));
		String plotName = "Simulated Annealing" + annealingModel.getNumCities() + "Cities " +  
				annealingModel.getNumIterations() + "Iterations";
		try {
			plot.save("plots" + "/" + plotName, "png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Image plotImg = new Image("file:plots/" + plotName + ".png");
		ImageView plotImgView = new ImageView(plotImg);
		pane.setCenter(plotImgView);
		
		Label explanation = new Label("Each point on the plot represents " + 
				(int)(annealingModel.getNumIterations() / 20.0) + " iterations");
		pane.setBottom(explanation);
	}
	
	private void drawCities() {
		map.getChildren().remove(citiesGroup);
		citiesGroup = new Group();
		ArrayList<City> cities = new ArrayList<City>(annealingModel.getCities().values());
		String startingCity = annealingModel.getStartingCity();
		for(City city: cities) {
			if(city.getId().equals(startingCity))
				drawCity(city, Color.BLUE);
			else
				drawCity(city, Color.RED);
		}
		map.getChildren().add(citiesGroup);
	}
	
	public void drawTour(Tour tour) {
		ArrayList<String> cities = new ArrayList<>(tour.getCities());
		String startingCity = annealingModel.getStartingCity();
		map.getChildren().remove(citiesGroup);
		citiesGroup = new Group();
		City prevCity = null;
		City currCity = null;
		for(String cityId: cities) {
			City city = annealingModel.getCity(cityId);
			currCity = city;
			if(city.getId().equals(startingCity))
				drawCity(city, Color.BLUE);
			else
				drawCity(city, Color.GREEN);
			if(prevCity != null)
				drawConnection(prevCity, currCity);
			prevCity = currCity;
		}
		map.getChildren().add(citiesGroup);
	}
	
	private void drawCity(City city, Color color) {
		Point2D location = city.getLocation();
		Circle circle = new Circle(location.getX(), location.getY(), 5);
		circle.setFill(color);
		circle.setStroke(Color.BLACK);
		citiesGroup.getChildren().add(circle);
	}
	
	private void drawConnection(City city1, City city2) {
		Point2D location1 = city1.getLocation();
		Point2D location2 = city2.getLocation();
		Line connection = new Line(location1.getX(), location1.getY(), location2.getX(), location2.getY());
		citiesGroup.getChildren().add(connection);
	}
	
	private void drawIsing() {
		root = new BorderPane();
		root.setPadding(new Insets(10, 20, 10, 20));
		drawIsingTop();
		drawIsingLattice();
		drawIsingButtons();
		setScene();
		setStage();
	}
	
	private void drawIsingTop() {
		HBox top = new HBox(800);
		
		Button back = new Button("Back");
		back.setOnAction(e -> {
			drawMenu();
		});
		
		Label title = new Label("The Ising Model");
		title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		
		top.getChildren().addAll(back, title);
		root.setTop(top);
	}
	
	private void drawIsingLattice() {
		lattice = new Group();
		Rectangle rect = new Rectangle(MAP_HEIGHT, MAP_HEIGHT);
		rect.setFill(Color.WHITE);
		rect.setStroke(Color.BLACK);
		lattice.getChildren().add(rect);
		root.setCenter(lattice);
	}
	
	public void fillIsingLattice(Spin[][] lattice) {
		this.lattice.getChildren().remove(sites);
		sites = new Group();
		int latticeSize = isingModel.getLatticeSize();
		double siteSize = (double) MAP_HEIGHT / (double) latticeSize;
		double x = 0;
		double y = 0;
		for(int i = 0; i < latticeSize; i++) {
			for(int j = 0; j < latticeSize; j++) {
				Rectangle site = new Rectangle(x, y, siteSize, siteSize);
				if(lattice[i][j] == Spin.POSITIVE)
					site.setFill(Color.WHITE);
				else
					site.setFill(Color.BLACK);
				sites.getChildren().add(site);
				x += siteSize;
			}
			x = 0;
			y += siteSize;
		}
		this.lattice.getChildren().add(sites);
	}
	
	private void drawIsingButtons() {
		HBox buttonPane = new HBox(50);
		
		Button settings = new Button("Settings");
		settings.setOnAction(e -> {
			isingSettingsDialog();
		});
		
		Button simulate = new Button("Simulate");
		simulate.setOnAction(e -> {
			isingModel.simulate();
		});
		
		Button viewSummary = new Button("View Summary");
		viewSummary.setOnAction(e -> {
			isingSummaryDialog();
		});
		
		Button estimateDistribution = new Button("Estimate Distribution");
		estimateDistribution.setOnAction(e -> {
			estimateDistributionDialog();
		});
		
		Button reset = new Button("Reset");
		reset.setOnAction(e -> {
			isingModel.reset();
		});
		
		buttonPane.getChildren().addAll(settings, simulate, viewSummary, estimateDistribution, reset);
		buttonPane.setAlignment(Pos.CENTER);
		root.setBottom(buttonPane);
		BorderPane.setAlignment(buttonPane, Pos.CENTER);
	}
	
	private void isingSettingsDialog() {
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("Ising Model Settings");
		
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		int rowNum = 0;
		
		grid.add(new Label("Lattice Size"), 0, rowNum);
		TextField size = new TextField();
		Button createLattice = new Button("Create Lattice");
		createLattice.setOnAction(e -> {
			try {
				int latticeSize = Integer.parseInt(size.getText());
				if(latticeSize > 0)
					isingModel.createLattice(latticeSize);
				else
					showAlert("Lattice Size Error", "Lattice size must be greater than zero");
			} catch(NumberFormatException exception) {
				showAlert("Lattice Size Error", "Lattice Size must be a number");
			}
		});
		
		grid.add(size, 1, rowNum++);
		grid.add(createLattice, 0, rowNum++);
		
		grid.add(new Label("Coupling Constant, Beta"), 0, rowNum);
		TextField beta = new TextField();
		beta.setText(String.valueOf(isingModel.getBeta()));
		Button setBeta = new Button("Set Beta");
		setBeta.setOnAction(e -> {
			try {
				double b = Double.parseDouble(beta.getText());
				if(b > 0)
					isingModel.setBeta(b);
				else
					showAlert("Beta Value Error", "The value of beta must be greater than zero");
			} catch(NumberFormatException exception) {
				showAlert("Beta Value Error", "Value of beta must be a number");
			}
		});
		
		grid.add(beta, 1, rowNum++);
		grid.add(setBeta, 0, rowNum++);
		
		grid.add(new Label("Number of Iterations"), 0, rowNum);
		TextField iterations = new TextField();
		iterations.setText(String.valueOf(isingModel.getNumIterations()));
		Button setIterations = new Button("Set Iterations");
		setIterations.setOnAction(e -> {
			try {
				int itr = Integer.parseInt(iterations.getText());
				if(itr > 0)
					isingModel.setNumIterations(itr);
				else
					showAlert("Iterations Error", "Number of iterations must be greater than zero");
			} catch(NumberFormatException exception) {
				showAlert("Iterations Error", "Number of iterations must be a number (obviously)");
			}
		});
		
		grid.add(iterations, 1, rowNum++);
		grid.add(setIterations, 0, rowNum++);
		
		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();
	}
	
	private void isingSummaryDialog() {
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("Ising Model Summary");
		dialog.setHeaderText("Summary of the changes to the configurations over time");
		
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(10, 20, 10, 20));
		drawConfigurationsTable(pane);
		drawConfigurationsPlot(pane);
		
		dialog.getDialogPane().setContent(pane);
		dialog.showAndWait();
	}
	
	private void drawConfigurationsTable(BorderPane pane) {
		TableView<Configuration> table = new TableView<Configuration>();
		ObservableList<Configuration> configurations = FXCollections.observableArrayList(
				isingModel.getConfigurations().values());
		
		TableColumn<Configuration, String> configNumCol = new TableColumn<Configuration, String>(
				"Iteration Number");
		configNumCol.setCellValueFactory(new PropertyValueFactory<>("iterationNum"));
		
		TableColumn<Configuration, String> positiveSpinProportionCol = new TableColumn<Configuration, 
				String>("Positive Spin Proportion");
		positiveSpinProportionCol.setCellValueFactory(new PropertyValueFactory<>("positiveSpinProportion"));
		
		TableColumn<Configuration, String> neighbourOppositeSpinProportionCol = 
				new TableColumn<Configuration, String>("Proportion of Neighbours with Opposite Spin");
		neighbourOppositeSpinProportionCol.setCellValueFactory(new PropertyValueFactory<>(
				"neighbourOppositeSpinProportion"));
		
		table.setItems(configurations);
		table.getColumns().addAll(configNumCol, positiveSpinProportionCol, 
				neighbourOppositeSpinProportionCol);
		table.prefHeightProperty().bind(Bindings.size(configurations).multiply(10));
		
		table.setRowFactory(rf -> {
			TableRow<Configuration> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if(!row.isEmpty() && event.getButton() == MouseButton.PRIMARY) {
					Configuration configuration = row.getItem();
					fillIsingLattice(configuration.getLattice());
				}
			});
			return row; 
		});
		
		pane.setTop(table);
	}
	
	private void drawConfigurationsPlot(BorderPane pane) {
		List<Double> iterations = new ArrayList<Double>();
		List<Double> positiveSpinProportions = new ArrayList<Double>();
		List<Double> neighbourOppositeSpinProportions = new ArrayList<Double>();
		for(Map.Entry<Integer, Configuration> entry: isingModel.getConfigurations().entrySet()) {
			iterations.add((double) entry.getKey());
			positiveSpinProportions.add((entry.getValue()).getPositiveSpinProportion());
			neighbourOppositeSpinProportions.add((entry.getValue()).getNeighbourOppositeSpinProportion());
		}
		
		/*
		double minPositiveSpinProportion = Collections.min(positiveSpinProportions);
		double maxPositiveSpinProportion = Collections.max(positiveSpinProportions);
		double minNeighbourOppositeSpinProportion = Collections.min(neighbourOppositeSpinProportions);
		double maxNeighbourOppositeSpinProportion = Collections.max(neighbourOppositeSpinProportions);
		*/
		
		Plot plot = Plot.plot(Plot.plotOpts().title("Positive Spin Proportion vs Iterations"));
		plot.xAxis("Iterations", Plot.axisOpts().range(0, iterations.size()));
		plot.yAxis("Positive Spin Proportion", Plot.axisOpts()
				.range(0, 1));
		plot.series("Data", Plot.data().xy(iterations, positiveSpinProportions), Plot.seriesOpts()
				.marker(Plot.Marker.NONE)
				.markerColor(java.awt.Color.BLACK)
				.color(java.awt.Color.BLACK));
		String posSpinPropPlot = "Positive Spin Proportion" + isingModel.getLatticeSize() + "Lattice Size " + 
				isingModel.getNumIterations() + "Iterations";
		try {
			plot.save("plots" + "/" + posSpinPropPlot, "png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		plot = Plot.plot(Plot.plotOpts().title("Neighbours With Opposite Spin vs Iterations"));
		plot.xAxis("Iterations", Plot.axisOpts().range(0, iterations.size()));
		plot.yAxis("Opposite Spin Proportion", Plot.axisOpts()
				.range(0, 1));
		plot.series("Data", Plot.data().xy(iterations, neighbourOppositeSpinProportions), Plot.seriesOpts()
				.marker(Plot.Marker.NONE)
				.markerColor(java.awt.Color.BLACK)
				.color(java.awt.Color.BLACK));
		String nOppSpinPropPlot = "Proportion of Neighbours With Opposite Spin" + 
				isingModel.getLatticeSize() + "Lattice Size " +  isingModel.getNumIterations() + 
				"Iterations";
		try {
			plot.save("plots" + "/" + nOppSpinPropPlot, "png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Image plot1Img = new Image("file:plots/" + posSpinPropPlot + ".png");
		ImageView plot1ImgView = new ImageView(plot1Img);
		pane.setLeft(plot1ImgView);
		
		Image plot2Img = new Image("file:plots/" + nOppSpinPropPlot + ".png");
		ImageView plot2ImgView = new ImageView(plot2Img);
		pane.setRight(plot2ImgView);
		
		Label explanation = new Label("Each point on the plot represents " + 
				(int)(isingModel.getNumIterations() / 10.0) + " iterations");
		pane.setBottom(explanation);
	}
	
	private void estimateDistributionDialog() {
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("Estimate Distribution");
		
		
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		grid.add(new Label("Number of states to estimate"), 0, 0);
		TextField numToEstimate = new TextField();
		int maxEstimate = (int) Math.pow(2, Math.pow(isingModel.getLatticeSize(), 2));
		numToEstimate.setPromptText("Between 1 and " + maxEstimate);
		grid.add(numToEstimate, 1, 0);
		Button estimate = new Button("Estimate");
		estimate.setOnAction(e -> {
			try {
				int num = Integer.parseInt(numToEstimate.getText());
				if(num > 0 && num < Math.pow(2, maxEstimate)) {
					dialog.close();
					distributionEstimateDialog(num);
				}
				else
					showAlert("Estimate Number Error", "The number of states to estimate must be between 1 and "
							+ maxEstimate);
			} catch(NumberFormatException exception) {
				showAlert("Estimate Number Error", "The number of states to estimate must be a number (obviously)");
			}
		});
		grid.add(estimate, 0, 1);
		
		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();
	}
	
	private void distributionEstimateDialog(int numToEstimate) {
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("State Distribution Estimate");
		
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(10, 20, 10, 20));
		
		List<Estimate> estimates = isingModel.estimate(numToEstimate);
		
		drawDistributionTable(pane, estimates);
		drawDistributionPlot(pane, estimates);
		
		dialog.getDialogPane().setContent(pane);
		dialog.show();
	}
	
	private void drawDistributionTable(BorderPane pane, List<Estimate> e) {
		TableView<Estimate> table = new TableView<Estimate>();
		ObservableList<Estimate> estimates = FXCollections.observableArrayList(e);
		
		TableColumn<Estimate, String> stateNumCol = new TableColumn<Estimate, String>(
				"State Number");
		stateNumCol.setCellValueFactory(new PropertyValueFactory<>("stateNum"));
		
		TableColumn<Estimate, String> probabilityCol = new TableColumn<Estimate, String>("Probability");
		probabilityCol.setCellValueFactory(new PropertyValueFactory<>("probability"));
		
		table.setItems(estimates);
		table.getColumns().addAll(stateNumCol, probabilityCol);
		table.prefHeightProperty().bind(Bindings.size(estimates).multiply(10));
		
		table.setRowFactory(rf -> {
			TableRow<Estimate> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if(!row.isEmpty() && event.getButton() == MouseButton.PRIMARY) {
					Estimate estimate = row.getItem();
					fillIsingLattice(estimate.getLattice());
				}
			});
			return row; 
		});
		
		pane.setLeft(table);
	}
	
	private void drawDistributionPlot(BorderPane pane, List<Estimate> estimates) {
		List<Double> states = new ArrayList<Double>();
		List<Double> probabilities = new ArrayList<Double>();
		double stateNum = 0;
		for(Estimate estimate: estimates) {
			states.add(stateNum);
			stateNum++;
			probabilities.add(estimate.getProbability());
		}
	
		double maxProbability = Collections.max(probabilities);
		if(maxProbability < 1)
			maxProbability = 1;
		Plot plot = Plot.plot(Plot.plotOpts().title("Probability Distribution of States"));
		plot.xAxis("States", Plot.axisOpts().range(0, states.size()));
		plot.yAxis("Probability", Plot.axisOpts().range(0, maxProbability + (maxProbability / 20.0)));
		plot.series("Data", Plot.data().xy(states, probabilities), Plot.seriesOpts()
				.marker(Plot.Marker.CIRCLE)
				.markerColor(java.awt.Color.BLACK)
				.color(java.awt.Color.BLACK));
		String plotName = "Ising Model State Distribution Estimate " + "LatticeSize" + isingModel.getLatticeSize() +
				" Beta" + isingModel.getBeta();
		try {
			plot.save("plots" + "/" + plotName, "png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Image plotImg = new Image("file:plots/" + plotName + ".png");
		ImageView plotImgView = new ImageView(plotImg);
		pane.setRight(plotImgView);
	}
	
	private void drawQueueing() {
		root = new BorderPane();
		root.setPadding(new Insets(10, 20, 10, 20));
		drawQueueingTop();
		drawQueueingButtons();
		setScene();
		setStage();
	}
	
	private void drawQueueingTop() {
		HBox top = new HBox(750);
		
		Button back = new Button("Back");
		back.setOnAction(e -> {
			drawMenu();
		});
		
		Label title = new Label("Simulation of an M/M/1 Queue");
		title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		
		top.getChildren().addAll(back, title);
		root.setTop(top);
	}
	
	private void drawQueueingButtons() {
		VBox buttonPane = new VBox(50);
		
		Button settings = new Button("Settings");
		settings.setOnAction(e -> {
			queueSettingsDialog();
		});
		
		Button simulate = new Button("Simulate");
		simulate.setOnAction(e -> {
			queue.simulate();
		});
		
		Button viewSummary = new Button("View Summary");
		viewSummary.setOnAction(e -> {
			queueSummaryDialog();
		});
		
		buttonPane.getChildren().addAll(settings, simulate, viewSummary);
		buttonPane.setAlignment(Pos.CENTER);
		root.setCenter(buttonPane);
		BorderPane.setAlignment(buttonPane, Pos.CENTER);
	}
	
	private void queueSettingsDialog() {
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("Queue Settings");
		
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		int rowNum = 0;
		
		Label ratesLabel = new Label("Rate Settings");
		ratesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		grid.add(ratesLabel, 0, rowNum++);
		
		grid.add(new Label("Arrival Rate"), 0, rowNum);
		TextField arrivalRate = new TextField(String.valueOf(queue.getArrivalRate()));
		grid.add(arrivalRate, 1, rowNum++);
		
		Label serviceRates = new Label("Service Rates");
		grid.add(serviceRates, 0, rowNum);
		TextField serviceRate1 = new TextField(String.valueOf(queue.getServiceRates()[0]));
		TextField serviceRate2 = new TextField(String.valueOf(queue.getServiceRates()[1]));
		grid.add(serviceRate1, 1, rowNum);
		grid.add(serviceRate2, 2, rowNum++);
		
		Button setServiceRates = new Button("Set Service Rates");
		setServiceRates.setOnAction(e -> {
			try {
				double aRate = Double.parseDouble(arrivalRate.getText());
				double sRate1 = Double.parseDouble(serviceRate1.getText());
				double sRate2 = Double.parseDouble(serviceRate2.getText());
				if(aRate > 0 && (sRate1 > 0 || sRate2 > 0)) {
					if(!(sRate1 <= 0 && sRate2 <= 0)) {
						double sRates[] = {sRate1, sRate2};
						queue.setRates(aRate, sRates);
					}
					else
						showAlert("Queue Rate Error", "At least one of the service rates must be greater "
								+ "than zero");
				}
				else
					showAlert("Queue Rate Error", "The rates must all be greater than or equal to zero");
			} catch(NumberFormatException exception) {
				showAlert("Queue Rate Error", "The rates must all be numbers");
			}
		});
		grid.add(setServiceRates, 0, rowNum++);
		
		rowNum++;
		Label algorithmLabel = new Label("AlgorithmSettings Settings");
		algorithmLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		grid.add(algorithmLabel, 0, rowNum++);
		
		grid.add(new Label("Running Time"), 0, rowNum);
		TextField maxTime = new TextField(String.valueOf(queue.getMaxTime()));
		grid.add(maxTime, 1, rowNum);
		Button setMaxTime = new Button("Set Running Time");
		setMaxTime.setOnAction(e -> {
			try {
				double time = Double.parseDouble(maxTime.getText());
				if(time > 0)
					queue.setMaxTime(time);
				else
					showAlert("Running Time Error", "The running time must be greater than zero");
			} catch(NumberFormatException exp) {
				showAlert("Running Time Error", "The running time must be a number");
			}
		});
		grid.add(setMaxTime, 2, rowNum++);
		
		grid.add(new Label("Maximum Number of Copies"), 0, rowNum);
		TextField maxCopies = new TextField(String.valueOf(queue.getMaxCopiesAllowed()));
		grid.add(maxCopies, 1, rowNum);
		Button setMaxCopies = new Button("Set Maximum Copies");
		setMaxCopies.setOnAction(e -> {
			try {
				int copies = Integer.parseInt(maxCopies.getText());
				if(copies > 0)
					queue.setMaxCopiesAllowed(copies);
				else
					showAlert("Maximum Copies Error", "The maximum number of copies must be greater than zero");
			} catch(NumberFormatException exp) {
				showAlert("Running Time Error", "The maximum number of copies must be a number");
			}
		});
		grid.add(setMaxCopies, 2, rowNum++);
		
		grid.add(new Label("Number of Servers"), 0, rowNum);
		TextField numServers = new TextField(String.valueOf(queue.getNumServers()));
		numServers.setDisable(true);
		grid.add(numServers, 1, rowNum);
		Button setNumServers = new Button("Set Number of Servers");
		setNumServers.setOnAction(e -> {
			try {
				int servers = Integer.parseInt(numServers.getText());
				if(servers > 0)
					queue.setMaxTime(servers);
				else
					showAlert("Number of Servers Error", "The number of servers must be greater than zero");
			} catch(NumberFormatException exp) {
				showAlert("Number of Servers Error", "The number of servers must be a number");
			}
		});
		setNumServers.setDisable(true);
		grid.add(setNumServers, 2, rowNum++);
		
		grid.add(new Label("Queue Type"), 0, rowNum);
		QueueType[] queueTypes = QueueType.values();
		String[] types = new String[queueTypes.length];
		for(int i = 0; i < queueTypes.length; i++)
			types[i] = queueTypes[i].getType();
		ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(types));
		comboBox.getSelectionModel().select(0);
		grid.add(comboBox, 1, rowNum);

		Button setQueueType = new Button("Set Queue Type");
		setQueueType.setOnAction(e -> {
			queue.setQueueType(comboBox.getValue());
		});
		grid.add(setQueueType, 2, rowNum++);
		
		dialog.getDialogPane().setContent(grid);
		dialog.showAndWait();
	}
	
	private void queueSummaryDialog() {
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("Queue Summary");
		dialog.setHeaderText("Summary of the queue over time");
		
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(10, 20, 10, 20));
		
		HBox top = new HBox(150);
		Label averageInSystem = new Label("Average number of customers in the system: " + 
				queue.getAverageInSystem());
		Label averageSojournTime = new Label("Average sojourn time: " + queue.getAverageSojournTime());
		top.getChildren().addAll(averageInSystem, averageSojournTime);
		pane.setTop(top);
		
		drawQueueTable(pane);
		drawQueuePlot(pane);
		
		dialog.getDialogPane().setContent(pane);
		dialog.showAndWait();
	}
	
	private void drawQueueTable(BorderPane pane) {
		TableView<Time> table = new TableView<Time>();
		ObservableList<Time> times = FXCollections.observableArrayList(queue.getTimes());
		
		TableColumn<Time, String> timeCol = new TableColumn<Time, String>("Time");
		timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
		
		TableColumn<Time, String> numInSystemCol = new TableColumn<Time, String>("Number in the System");
		numInSystemCol.setCellValueFactory(new PropertyValueFactory<>("numInSystem"));
		
		table.setItems(times);
		table.getColumns().addAll(timeCol, numInSystemCol);
		table.prefHeightProperty().bind(Bindings.size(times).multiply(55));
		
		pane.setLeft(table);
	}
	
	private void drawQueuePlot(BorderPane pane) {
		List<Double> times = new ArrayList<Double>();
		List<Double> numInSystem = new ArrayList<Double>();
		for(Map.Entry<Double, Integer> entry: queue.getInSystem().entrySet()) {
			times.add(entry.getKey());
			numInSystem.add((double) entry.getValue());
		}
		
		double maxTime = Collections.max(times);
		double maxNumInSystem = Collections.max(numInSystem);
		Plot plot = Plot.plot(Plot.plotOpts().title("Number in the System vs Time"));
		plot.xAxis("Time", Plot.axisOpts().range(0, maxTime));
		plot.yAxis("Number in System", Plot.axisOpts()
				.range(0, maxNumInSystem + (maxNumInSystem / 20.0)));
		plot.series("Data", Plot.data().xy(times, numInSystem), Plot.seriesOpts()
				.marker(Plot.Marker.NONE)
				.markerColor(java.awt.Color.BLACK)
				.color(java.awt.Color.BLACK));
		String plotName = "Queueing " + "Arrival Rate" + queue.getArrivalRate() + " RunningTime" +
				queue.getMaxTime();
		try {
			plot.save("plots" + "/" + plotName, "png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Image plotImg = new Image("file:plots/" + plotName + ".png");
		ImageView plotImgView = new ImageView(plotImg);
		pane.setRight(plotImgView);
	}
	
	public void showAlert(String title, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	private void setScene() {
		bounds = root.getLayoutBounds();
		scene = new Scene(root);
	}
	
	private void setStage() {
		Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
	    double factor = Math.min(visualBounds.getWidth() / (bounds.getWidth() + MARGIN),
	            visualBounds.getHeight() / (bounds.getHeight() + MARGIN));
	    primaryStage.setScene(scene);
	    primaryStage.setWidth((bounds.getWidth() + MARGIN) * factor);
	    primaryStage.setHeight((bounds.getHeight() + MARGIN) * factor);
	    primaryStage.setTitle("STAT 3506 Final Project");
	    //primaryStage.setMaximized(true);
	    primaryStage.show();
	}
	
	public static void main(String args[]) {
		launch(args);
	}
}
