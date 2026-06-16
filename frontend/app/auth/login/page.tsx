"use client";

import { handleEmailLogIn, handleGoogleAuth } from "@/lib/api/auth";
import { useState } from "react";
import { FcGoogle } from "react-icons/fc";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

function Page() {
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");

  return (
    <div className="flex min-h-screen w-full items-center justify-center">
      <Card className="w-full h-fit max-w-md">
        <CardHeader className="pb-2">
          <CardTitle className="header-font text-center text-4xl font-normal">
            Login
          </CardTitle>
        </CardHeader>
        <CardContent className="flex flex-col gap-5 pt-4">
          <Button
            variant="outline"
            className="w-full gap-2 py-5 text-base"
            onClick={() => handleGoogleAuth()}
          >
            <FcGoogle className="text-xl" />
            Continue with Google
          </Button>

          <div className="flex items-center gap-3">
            <Separator className="flex-1" />
            <span className="text-muted-foreground text-sm">Or</span>
            <Separator className="flex-1" />
          </div>

          <div className="flex flex-col gap-4">
            <div className="flex flex-col gap-2">
              <Label htmlFor="email" className="text-base">
                Email
              </Label>
              <Input
                id="email"
                type="text"
                placeholder="Enter email"
                className="py-5 text-base"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="password" className="text-base">
                Password
              </Label>
              <Input
                id="password"
                type="password"
                placeholder="Enter password"
                className="py-5 text-base"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>

          <Button
            className="w-full py-5 text-base"
            onClick={() => handleEmailLogIn({ email, password })}
          >
            Login
          </Button>
          <p className="text-center text-sm text-muted-foreground">
            Not registered?{" "}
            <a
              href="/auth/register"
              className="text-primary font-medium hover:underline"
            >
              Sign up
            </a>
          </p>
        </CardContent>
      </Card>
    </div>
  );
}

export default Page;
