package edu.technopolis.homework.messenger.net;

/**
 * класс, вызывающий команды
 */
public class Invoker {
    private Command loginCommand;
    private Command textCommand;

    public Invoker(Command loginCommand, Command textCommand) {
        this.loginCommand = loginCommand;
        this.textCommand = textCommand;
    }

    public void login() {
        //loginCommand.execute();
    }

}
