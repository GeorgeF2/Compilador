import javafx.application.Application;
import java.net.URL;
import javafx.geometry.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.*;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.shape.*;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.util.EventObject;
import java.lang.Object;
import javafx.event.Event;
import javafx.event.EventHandler;
import javax.xml.bind.DatatypeConverter;

public class Compilador extends Application {

TextArea inputArea = new TextArea();
TextArea runTerminal = new TextArea();
TreeView<String> tree = new TreeView<String>();
BorderPane bp = new BorderPane();


@Override
public void start(Stage stage){

	MenuBar mBar = new MenuBar();
	Menu smFile = new Menu("File");
	Menu smEdit = new Menu("Edit");
	Menu smWindow = new Menu("Window");
	Menu smHelp = new Menu("Help");

	MenuItem miOpen = new MenuItem("Open");
	MenuItem miSave = new MenuItem("Save");
	MenuItem miUndo = new MenuItem("Undo");
	MenuItem miRedo = new MenuItem("Redo");
	MenuItem miCut = new MenuItem("Cut");
	MenuItem miCopy = new MenuItem("Copy");
	MenuItem miPaste = new MenuItem("Paste");
	MenuItem miClear = new MenuItem("Clear");
	MenuItem miMinimize = new MenuItem("Minimize");
	MenuItem miClose = new MenuItem("Close");
	MenuItem miTerms = new MenuItem("Terms of Use");
	MenuItem miReport = new MenuItem("Report Issue");
	MenuItem miWelcome = new MenuItem("Welcome Guide");

	miOpen.setOnAction(e->{
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(stage);
			try {
			    String data = new String();
			    Scanner scanner = new Scanner(file);
			    while(scanner.hasNextLine()) {
			        data += scanner.nextLine();
			        data += "\n";
				}
			    inputArea.setText(data.toString());
			    scanner.close();
			} catch (FileNotFoundException e1) {
			    e1.printStackTrace();
			}

			TreeItem<String> rootItem = new TreeItem<String> (file.getParentFile().getName(), new ImageView(new Image("folder.jpeg")));
			rootItem.setExpanded(true);
			for (File subFile : file.getParentFile().listFiles()) {
			    if (subFile.isFile()) {
			        TreeItem<String> item = new TreeItem<String> (subFile.getName(), new ImageView(new Image("file.png")));
			        rootItem.getChildren().add(item);
				}
			}
			tree = new TreeView<String> (rootItem);
			tree.setOnMouseClicked(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent mouseEvent){
				    if(mouseEvent.getClickCount() == 2) {
				        TreeItem<String> item = tree.getSelectionModel().getSelectedItem();
				        File file = new File(item.getValue());
				        try {
				            String data = new String();
				            Scanner scanner = new Scanner(file);
				            while(scanner.hasNextLine()) {
				                data += scanner.nextLine();
				                data += "\n";
							}
				            inputArea.setText(data.toString());
				            scanner.close();
						} catch (FileNotFoundException e1) {
				            e1.printStackTrace();
						}
				        System.out.println("Selected Text : " + item.getValue());
					}
				}
			});
			bp.setLeft(tree);
		});


	smFile.getItems().addAll( miOpen, miSave);
	smEdit.getItems().addAll(miUndo, miRedo, miCut, miCopy, miPaste, miClear);
	smWindow.getItems().addAll(miMinimize, miClose);
	smHelp.getItems().addAll(miTerms, miReport, miWelcome);
	mBar.getMenus().addAll(smFile, smEdit, smWindow, smHelp);
	//VBox alls = new VBox(mBar);



	inputArea.setStyle("-fx-control-inner-background: darkgray; -fx-font-family: Menlo; -fx-text-fill: white;");
	inputArea.setPadding(new Insets(5, 5, 5, 5));

	Button submit = new Button("Compile");
	submit.addEventHandler(MouseEvent.MOUSE_CLICKED, (event)->compileCode());
	HBox buttons = new HBox();
	buttons.setPadding(new Insets(5, 5, 5, 5));
	buttons.setAlignment(Pos.CENTER_RIGHT);
	buttons.getChildren().addAll(submit);

	VBox bottom = new VBox();
	runTerminal.setEditable(false);
	runTerminal.setStyle("-fx-control-inner-background: black; -fx-font-family: Courier; -fx-text-fill: white;");
	bottom.setPadding(new Insets(5, 5, 5, 5));
	bottom.getChildren().addAll(runTerminal, buttons);

	bp.setTop(mBar);
	bp.setLeft(tree);
	bp.setCenter(inputArea);
	bp.setBottom(bottom);

	//alls.getChildren().addAll(bp);


	Scene scene = new Scene(bp, 1000, 600);
	stage.setTitle("Compilador Intel Hex 80");
	stage.setScene(scene);
	stage.show();

}

private void compileCode(){
	System.out.println(inputArea.getText());


	String output = ":";     // = ":BCAAAADTDDDDDDDDDDDDDDDDCS" BC=Bit Count, A=Address, DT=Data Type, D=Data, CS=Check Sum
    String adr = "0100";

    int count = 0;
    String data = ":";
	for (String line : inputArea.getText().split("\n")) {
		String[] ins = line.split(" ");
        int subCount = 1;
		switch (ins[0].toLowerCase()) {
		case "nop":
			data = data + ("00");
			break;
		case "inc":
			data = data + ("04");
			break;
		case "div":
			data = data + ("84");
			break;
		case "dec":
			data = data + ("14");
			break;
		case "mul":
			data = data + ("A4");
			break;
		case "acall":
			String temp = ins[1].substring(0, 3) + "10001" + ins[1].substring(3);
            temp = binaryToHex(temp);
			data = data + (temp);
            subCount = 2;
			break;
		case "ret":
			data = data + ("22");
			break;
		case "push":
			data = data + ("C0");
			break;
		case "pop":
			data = data + ("D0");
			break;
		case "clr":
			data = data + ("E4");
			break;
		case "or":
			data = data + ("22" + ins[1].substring(0, 1));
            subCount = 2;
			break;

		default:
			break;
		}
        count += subCount;
        if (count >= 16) { // = ":BCAAAADTDDDDDDDDDDDDDDDDCS" BC=Bit Count, A=Address, DT=Data Type, D=Data, CS=Check Sum
            output = output + (Integer.toString(count, 16).toUpperCase() + adr + "00" + data.substring(1).toUpperCase());
            output = output + ("CS\n:");
            count = 0;
            adr = addAdr(adr, count);
            data = ":";
        }
	}
    if (data.length() > 1){
        output = output + (Integer.toString(data.length()/2, 16).toUpperCase() + adr + "00" + data.substring(1).toUpperCase());
        output = output + ("CS\n");
        output = output + (":00000001FF");
    }
    runTerminal.setText(output);
}

public static String addAdr(String adr, int count){
    return adr;
}
public static String binaryToHex(String s){
	int decimal = Integer.parseInt(s,2);
	String hexStr = Integer.toString(decimal,16);
    return hexStr;
}

public static String hexStringToByteArray(String s) {
	String result = "";
	for (char c : s.toCharArray()) {
		int i = Integer.parseInt(String.valueOf(c));
		String bin = Integer.toBinaryString(i);
		while (bin.length() < 4)
			bin = "0" + bin;
		result = result + bin;
	}
	return result;
}

public static void main(String[] args) {
	launch(args);
}

}
