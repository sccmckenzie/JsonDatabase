package server;

class Controller {
    private Command command;

    void setCommand(Command command) {
        this.command = command;
    }

    void executeCommand() throws Exception {
        command.execute();
    }
}
