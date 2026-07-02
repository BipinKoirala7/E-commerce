"use client";

import Image from "next/image";

import { User } from "@/types";
import { useUser } from "@/Context/UserProvider";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import toast from "react-hot-toast";
import { UserRound } from "lucide-react";

export default function Account() {
  const user: User | null = useUser().data;
  if (!user) return null;

  return (
    <div className="min-h-screen w-full flex items-center justify-center p-6  from-indigo-50 via-white to-purple-50 dark:from-zinc-950 dark:via-zinc-900 dark:to-black">
      <Card className="w-full max-w-md relative mt-14 overflow-visible rounded-3xl border border-slate-200/60 dark:border-zinc-800 bg-white/80 dark:bg-zinc-900/70 backdrop-blur-xl shadow-2xl shadow-slate-200/50 dark:shadow-black/20 transition-all duration-300 hover:scale-[1.01]">
        <div className="absolute left-1/2 top-0 -translate-x-1/2 -translate-y-1/2 z-20">
          <div className="relative">
            <div className="absolute inset-0 rounded-full blur-xl bg-indigo-300/30 scale-110" />
            <div className="relative p-1.5 bg-white dark:bg-zinc-900 rounded-full shadow-xl border border-slate-200 dark:border-zinc-700">
              {user.profilePictureUrl ? (
                <Image
                  src={user.profilePictureUrl}
                  alt="Profile Picture"
                  width={110}
                  height={110}
                  className="rounded-full object-cover aspect-square"
                />
              ) : (
                <div className="flex items-center justify-center p-4">
                  <UserRound className="rounded-full w-full h-full text-green1" />
                </div>
              )}
            </div>
          </div>
        </div>

        <CardContent className="pt-20 pb-6 px-7 space-y-6">
          <div className="text-center space-y-2">
            <h2 className="text-2xl font-bold tracking-tight text-slate-800 dark:text-white">
              {user.userName}
            </h2>

            <p className="text-sm text-slate-500 dark:text-zinc-400">
              @{user.userName.toLowerCase()}
            </p>

            <Badge
              variant="secondary"
              className="rounded-full px-4 py-1 text-xs font-medium tracking-wide"
            >
              {user.role}
            </Badge>
          </div>

          <div className="space-y-2 border-t pt-5 dark:border-zinc-800">
            <label className="text-xs uppercase tracking-widest font-semibold text-slate-400 dark:text-zinc-500">
              Email Address
            </label>

            <div className="rounded-2xl border border-slate-200 dark:border-zinc-800 bg-slate-50/80 dark:bg-zinc-900 p-4 flex justify-between items-center gap-3">
              <span className="text-sm font-medium text-slate-700 dark:text-zinc-300 truncate">
                {user.email}
              </span>

              <span
                className={`text-xs font-semibold px-3 py-1 rounded-full border ${
                  user.emailVerified
                    ? "bg-emerald-500/10 text-emerald-600 border-emerald-500/20"
                    : "bg-rose-500/10 text-rose-600 border-rose-500/20"
                }`}
              >
                {user.emailVerified ? "Verified" : "Unverified"}
              </span>
            </div>

            {!user.emailVerified && (
              <Button
                className="w-full rounded-xl h-11 mt-2 transition-all shadow-md"
                onClick={() =>
                  toast.loading("Email verification is under development", {
                    duration: 2000,
                  })
                }
              >
                Verify Email Address
              </Button>
            )}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="rounded-2xl p-4 bg-slate-50 dark:bg-zinc-900 border border-slate-200 dark:border-zinc-800">
              <span className="text-[11px] uppercase font-semibold tracking-widest text-slate-400 dark:text-zinc-500 block mb-1">
                Last Login
              </span>

              <span className="text-sm font-semibold text-slate-700 dark:text-zinc-300">
                {new Date(user.lastLoginAt).toLocaleDateString(undefined, {
                  month: "short",
                  day: "numeric",
                  year: "numeric",
                })}
              </span>
            </div>

            <div className="rounded-2xl p-4 bg-slate-50 dark:bg-zinc-900 border border-slate-200 dark:border-zinc-800">
              <span className="text-[11px] uppercase font-semibold tracking-widest text-slate-400 dark:text-zinc-500 block mb-1">
                Status
              </span>

              <span className="inline-flex items-center text-sm font-semibold text-emerald-600 dark:text-emerald-400">
                <span className="h-2 w-2 rounded-full bg-emerald-500 mr-2 animate-pulse" />
                Active
              </span>
            </div>
          </div>

          <div className="border-t pt-4 dark:border-zinc-800">
            <div className="grid grid-cols-2 gap-3 text-xs text-slate-400 dark:text-zinc-500">
              <div className="space-y-1">
                <span className="uppercase tracking-wide font-medium block">
                  Created
                </span>
                <span>{new Date(user.createdAt).toLocaleDateString()}</span>
              </div>

              <div className="space-y-1 text-right">
                <span className="uppercase tracking-wide font-medium block">
                  Updated
                </span>
                <span>{new Date(user.updatedAt).toLocaleDateString()}</span>
              </div>
            </div>
          </div>

          <div className="pt-2 flex gap-3">
            <Button
              variant="outline"
              className="flex-1 rounded-xl"
              onClick={() =>
                toast.loading("Edit Profile Page is under development", {
                  duration: 2000,
                })
              }
            >
              Edit Profile
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
