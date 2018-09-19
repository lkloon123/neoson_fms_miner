package app.Controllers;

import app.Helper.ApiClient;
import app.Helper.ApiWrapper;
import app.Helper.SettingHelper;
import app.Interface.Logging;
import app.Models.Miner;
import app.Models.Response.GetResponse;

import java.io.FilterInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class SetupController implements Logging {
    private String apiToken = SettingHelper.apiToken;
    private int MAX_RETRIES = 3;
    private boolean isSetup = false;

    public SetupController() {
        if (this.apiToken == null) {
            this.setupToken();
        } else {
            this.validateApiToken();
        }
    }

    private void setupToken() {
        try (Scanner userInput = new Scanner(new FilterInputStream(System.in) {
            @Override
            public void close() throws IOException {
            }
        })) {
            //required user to enter api token
            System.out.print("Please enter your miner API Token : ");
            this.apiToken = userInput.nextLine();
            this.isSetup = true;
        }
        this.validateApiToken();
    }

    private void validateApiToken() {
        logger.info("Validating API Token...");

        new ApiWrapper<GetResponse<Miner>>().execute(ApiClient.getInterface().getMiner(this.apiToken),
                (data, error) -> {
                    if (error != null) {
                        if (error.errorData.code.equalsIgnoreCase("404")) {
                            setupToken();
                        } else {
                            //exceptions error, retry
                            if (MAX_RETRIES > 0) {
                                logger.info("Retrying");
                                MAX_RETRIES--;
                                validateApiToken();
                            } else {
                                logger.fatal("Failed After retries");
                                System.out.println("Exit in 3 seconds");
                                try {
                                    Thread.sleep(3 * 1000);
                                    System.exit(0);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        }
                    } else {
                        logger.info("API Token Validated!!");
                        if (isSetup) {
                            SettingHelper.apiToken = apiToken;
                            SettingHelper.write();
                        }
                        logger.info("Your Miner Name : " + data.data.minerName);
                        if (SettingHelper.software != null && SettingHelper.exeName != null && SettingHelper.config != null) {
                            new RunMiningController(SettingHelper.software, SettingHelper.exeName, SettingHelper.config);
                        }

                        new ListeningController();
                    }
                });
    }
}
