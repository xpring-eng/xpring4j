import { credentials } from "grpc";
import { GetAccountRequest } from "../generated/get_account_request_pb";
import { AccountServiceClient } from "../generated/account_service_grpc_pb";

/* eslint-disable @typescript-eslint/no-floating-promises */
/* eslint-disable no-console */
/* eslint-disable @typescript-eslint/require-await */

async function runILPGetBalanceTest(): Promise<void> {
  const client = new AccountServiceClient (
    "127.0.0.1:6565",
    credentials.createInsecure()
  );

  const req = new GetAccountRequest();
  req.setAccountId("connie");
  client.getAccount(req, (error, response): void => {
    if (error != null || response == null) {
      console.log("ERRORED :(");
      console.log(error);
      return;
    }
    console.log(JSON.stringify(response));
  });
}

/*
async function runILPCreateAccountTest(): Promise<void> {
  const client = new IlpServiceClient(
    "127.0.0.1:6565",
    credentials.createInsecure()
  );

  const req = new CreateAccountRequest();
  req.setAccountid("Noah");
  req.setAssetcode("XRP");
  req.setAssetscale(9);
  req.setDescription("Noah's test account");
  req.setJwt("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1rTTJPRGMxUVRKR05qSXlRelJFUmtKQk5qRTNNMFpCTkRsRFJEQkVSVFF3UWpWRk5VSkNNdyJ9.eyJuaWNrbmFtZSI6Im5oYXJ0bmVyIiwibmFtZSI6Im5oYXJ0bmVyQGdtYWlsLmNvbSIsInBpY3R1cmUiOiJodHRwczovL2F2YXRhcnMwLmdpdGh1YnVzZXJjb250ZW50LmNvbS91LzQ0NDAzNDU_dj00IiwidXBkYXRlZF9hdCI6IjIwMTktMTItMjdUMTg6NTI6MDMuMTg1WiIsImVtYWlsIjoibmhhcnRuZXJAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImlzcyI6Imh0dHBzOi8veHByaW5nc2FuZGJveC5hdXRoMC5jb20vIiwic3ViIjoiZ2l0aHVifDQ0NDAzNDUiLCJhdWQiOiIwcjFmclo1OWV5bERzTUgzYWNWZVNKRDVLSTZwdUVobyIsImlhdCI6MTU3NzQ3MjczMywiZXhwIjoxNTc3NDc2MzMzfQ.Kzddgj_Zd4Ib7jq4avLCigzIxJJjuh8NZII0wKfT4lA1XEPc_HAUEhNCTRe--CHpaCOo6HLs4YQdTcC_0flWLXX7_Uz5zVCS9KVX__g3BJRvU_pZjzt51iBkA8zKKzmxMAd9w2g0Lq8SanqMDZq7TfNXb85ZFDBPAjPDLV42sgkWy0i0RADddEadmACuvnp_GC91RDLIMOPY8BCDF8JBB2RRZ_CsPRLlpMlGaQYAgs9fbquq6qPwHXoFxHVfsGg_xYi4W8uAc_J1qWxOPDUEWKf_HLAu0a-0_kbufn1xXqkHowHaR4VA90ljabFUR92ioWXYUlwvPaFKqzalkeDdpQ");

  client.createAccount(req, (error, response): void => {
    if (error != null || response == null) {
      console.log("ERRORED :(");
      console.log(error);
      return;
    }
    console.log(JSON.stringify(response));
  });
}
*/

runILPGetBalanceTest();
// runILPCreateAccountTest();
