package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class Controller {
    List list;
    Boolean min = true;
    @FXML
    private TextField functionForm;
    @FXML
    private TextField aForm;
    @FXML
    private TextField bForm;
    @FXML
    private TextField toleranceForm;
    @FXML
    private TextField iterationForm;
    @FXML
    private TextField timeForm;
    @FXML
    private TextField resultXForm;
    @FXML
    private TextField resultFunctionXForm;
    @FXML
    private TextField resultAbsForm;
    @FXML
    private TextField resultIterationForm;
    @FXML
    private TextField resultTimeForm;
    @FXML
    private RadioButton minRadioButton;
    @FXML
    private RadioButton maxRadioButton;
    @FXML
    private Label resultLabelForm;
    @FXML
    private ProgressIndicator progressIndicatorForm;
    @FXML
    private void Min(){
        min=true;
        maxRadioButton.setSelected(false);
    }
    @FXML
    private void Max(){
        min=false;
        minRadioButton.setSelected(false);
    }
    @FXML
    private void OpenExcel(){
    try{
        System.out.println(new File(".").getAbsolutePath());
    File file = new File("LookingForOneOptPoint.xlsx");
        String command = "excel \"" + file + "\"";
        Runtime.getRuntime().exec("cmd.exe /c start " + command);

    }
    catch (Exception err){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Error !!! ");
        alert.setContentText(String.valueOf(err.getMessage()));
        alert.showAndWait();
    }
    }
    @FXML
    private void ButtonSeach(){
        Task task= new Task(){
            @Override
            protected Object call(){
                try{
                    Method method = new Method();
                    method.startTime=System.currentTimeMillis();
                    if (functionForm.getText().isEmpty()) {
                        throw new Exception("Function empty !");
                    }
                    if (aForm.getText().isEmpty()) {
                        throw new Exception("End A empty !");
                    }
                    if (bForm.getText().isEmpty()) {
                        throw new Exception("End B empty !");
                    }
                    if (toleranceForm.getText().isEmpty()) {
                        throw new Exception("Tolerance empty !");
                    }
                    if (iterationForm.getText().isEmpty()) {
                        throw new Exception("Iteration limit empty !");
                    }
                    if (timeForm.getText().isEmpty()) {
                        throw new Exception("Time limit empty !");
                    }
                    if (new Integer(iterationForm.getText()) <= 0) {
                        throw new Exception("Iteration can not be 0 or negative !");
                    }
                    if (new Double(timeForm.getText()) <= 0) {
                        throw new Exception("Time can not be 0 or negative !");
                    }
                    progressIndicatorForm.setVisible(true);
                    method.function=functionForm.getText().toLowerCase();
                    method.a = new BigDecimal(Double.valueOf(aForm.getText().replace(',','.')));
                    method.b = new BigDecimal(Double.valueOf(bForm.getText().replace(',','.')));
                    method.tol = new BigDecimal(Double.valueOf(toleranceForm.getText().replace(',','.')));
                    method.iteration=Integer.valueOf(iterationForm.getText());
                    method.itermax=Integer.valueOf(iterationForm.getText());
                    method.timeLimit=Long.parseLong(timeForm.getText());
                    method.timeMax = Long.parseLong(timeForm.getText());
                    list = new Expression(method.function).getUsedVariables();
                    if(list.get(0)!=null){
                        method.perem = String.valueOf(list.get(0));
                    }
                    else{
                        throw new Exception("Error entering operator!");
                    }
                    method.r = new Expression("(1+sqrt(5))/2").eval();

                    method.latch = new CountDownLatch(1);
                    while(method.a.subtract(method.b).abs().compareTo(method.tol) > 0&& method.cond==0){
                        if (method.latch.getCount() != 1) {
                            method.latch = new CountDownLatch(1);
                        }
                        method.x1 = method.getA();
                        method.x2 = method.getB();
                        method.functionx1 = new Expression(method.function).with(method.perem,method.x1).eval();
                        method.functionx2 = new Expression(method.function).with(method.perem,method.x2).eval();
                        if(min==true){
                            method.SeachMin();
                        }
                        else{
                            method.SeachMax();
                        }
                        if (method.iter >= method.iteration) {
                            Platform.runLater(() -> {
                                        method.StartPause();
                                        progressIndicatorForm.setVisible(false);
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Confirmation Dialog");
                                        alert.setHeaderText("number of iteration reached iteration limit !");
                                        alert.setContentText("You want to add iteration ?");
                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK) {
                                            method.iteration = method.iteration + method.itermax;
                                            iterationForm.setText(String.valueOf(method.iteration));
                                            progressIndicatorForm.setVisible(true);
                                        } else {
                                            method.cond = 1;
                                            resultLabelForm.setText("Solution not found, number of iteration reached by iteration limit !");
                                        }
                                        method.latch.countDown();
                                        method.StopPause();
                                    }
                            );
                            method.latch.await();
                        }
                        if ((System.currentTimeMillis() - method.startTime) >= method.timeLimit) {
                            Platform.runLater(() -> {
                                        method.StartPause();
                                        progressIndicatorForm.setVisible(false);
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Confirmation Dialog");
                                        alert.setHeaderText("The time exceeded !");
                                        alert.setContentText("You want to add time ?");
                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK) {
                                            method.timeLimit = method.timeLimit + method.timeMax;
                                            timeForm.setText(String.valueOf(method.timeLimit));
                                            progressIndicatorForm.setVisible(true);
                                        } else {
                                            method.cond = 1;
                                            resultLabelForm.setText("Solution not found, time exceeded !");
                                        }
                                        method.latch.countDown();
                                        method.StopPause();
                                    }

                            );
                            method.latch.await();
                        }
                        Thread.sleep(15);
                    }
                    Platform.runLater(() -> {
                        if (method.cond == 0) {
                            resultLabelForm.setText("Solution found!");
                        }
                        method.resultTime = System.currentTimeMillis() - method.startTime;
                        resultXForm.setText(String.valueOf((method.x1)));
                        resultFunctionXForm.setText(String.valueOf(method.functionx1.setScale(34, RoundingMode.UP)));
                        resultAbsForm.setText(String.valueOf(method.b.subtract(method.a).abs().setScale(34, RoundingMode.UP)));
                        resultIterationForm.setText(String.valueOf(method.iter));
                        resultTimeForm.setText(String.valueOf(method.resultTime));
                        progressIndicatorForm.setVisible(false);
                    });

                }
               catch (Exception ex){
                   try{
                       Platform.runLater(() -> {
                           resultXForm.setText("");
                           resultFunctionXForm.setText("");
                           resultAbsForm.setText("");
                           resultIterationForm.setText("");
                           resultTimeForm.setText("");
                           progressIndicatorForm.setVisible(false);
                           Alert alert = new Alert(Alert.AlertType.ERROR);
                           alert.setTitle("Error Dialog");
                           alert.setHeaderText("Error !!! ");
                           alert.setContentText(String.valueOf(ex.getMessage()));
                           alert.showAndWait();
                           resultLabelForm.setText(String.valueOf(ex.getMessage()));
                       });
                   }
                   catch (Exception ignored){
                       System.out.println(ignored.getMessage());
                   }
               }
                return null;
            }
        };
    Thread thread = new Thread(task);
    thread.start();
    Platform.runLater(()->ButtonClean());
    }
    @FXML
    private void ButtonClean(){
        resultXForm.clear();
        resultFunctionXForm.clear();
        resultAbsForm.clear();
        resultIterationForm.clear();
        resultTimeForm.clear();
        resultLabelForm.setText("");
    }
}
