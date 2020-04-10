package listener;

import com.oocourse.TimableOutput;
import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;
import controller.Controller;
import datacenter.ListenerToController;
import source.PersonInfo;

public class Listener {
    public static void main(String[] args) throws Exception {
        TimableOutput.initStartTimestamp();
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        ListenerToController ltc = new ListenerToController();
        PersonInfo.setLtc(ltc);
        Controller controller = new Controller(ltc);
        controller.addElevator("A", "A");
        controller.addElevator("B", "B");
        controller.addElevator("C", "C");
        controller.start();
        while (true) {
            Request request = elevatorInput.nextRequest();
            // when request == null
            // it means there are no more lines in stdin
            if (request == null) {
                break;
            } else {
                // a new valid request
                if (request instanceof PersonRequest) {
                    // a PersonRequest
                    PersonInfo tmp = new PersonInfo((PersonRequest) request);
                    ltc.add(tmp, false);
                    //System.out.println("A PersonRequest:    " + request);
                } else if (request instanceof ElevatorRequest) {
                    // an ElevatorRequest
                    String id = ((ElevatorRequest) request).getElevatorId();
                    String type = ((ElevatorRequest) request).getElevatorType();
                    controller.addElevator(id, type);
                    //System.out.println("An ElevatorRequest: " + request);
                }
            }
        }
        elevatorInput.close();
        ltc.setCanStop();
    }
}
