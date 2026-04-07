package gitv;

import gitv.cli.CommandRouter;

public class Main {
    public static void main(String[] args) {
        CommandRouter router = new CommandRouter();
        router.route(args);
    }
}
