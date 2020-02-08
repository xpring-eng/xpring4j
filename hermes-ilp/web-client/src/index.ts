import { IlpServiceClient } from "../generated/ilp_as_a_service_grpc_web_pb.js";
import { AccountId } from "../generated/accounts_pb";

/* eslint-disable @typescript-eslint/no-floating-promises */
/* eslint-disable no-console */
/* eslint-disable @typescript-eslint/require-await */

/** Export a test function in the browser. */
export async function runILPTest(): Promise<void> {
  const client = new IlpServiceClient("http://127.0.0.1:6565");

  const req = new AccountId();
  client.getBalance(req, undefined, (error, response): void => {
    if (error != null || response == null) {
      console.log("ERRORED :(");
      console.log(error);
      return;
    }
    console.log(JSON.stringify(response));
  });
}

/** Export any other classes the browser may want. */
export { IlpServiceClient } from "../generated/ilp_as_a_service_grpc_web_pb";
