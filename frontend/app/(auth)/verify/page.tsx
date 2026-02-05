"use client"

import React from "react"

import { useState, Suspense } from "react"
import Link from "next/link"
import { useSearchParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { verifyUser } from "@/lib/auth"
import { Loader2, Check, Mail } from "lucide-react"

function VerifyForm() {
  const searchParams = useSearchParams()
  const emailParam = searchParams.get("email") || ""
  
  const [email, setEmail] = useState(emailParam)
  const [code, setCode] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState("")
  const [success, setSuccess] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError("")

    const result = await verifyUser(email, code)

    if (result.success) {
      setSuccess(true)
    } else {
      setError(result.message || "Verification failed. Please try again.")
    }

    setIsLoading(false)
  }

  if (success) {
    return (
      <div className="w-full max-w-md space-y-8 text-center">
        <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-secondary/20">
          <Check className="h-8 w-8 text-secondary" />
        </div>
        <h1 className="text-3xl font-bold tracking-tight text-foreground">Email verified</h1>
        <p className="text-muted-foreground">
          Your email has been successfully verified. Sign in to complete your profile setup.
        </p>
        <Button asChild className="w-full bg-primary text-primary-foreground hover:bg-primary/90">
          <Link href="/login?onboarding=true">Continue to Login</Link>
        </Button>
      </div>
    )
  }

  return (
    <div className="w-full max-w-md space-y-8">
      <div className="text-center">
        <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-full bg-accent/20">
          <Mail className="h-8 w-8 text-accent" />
        </div>
        <h1 className="text-3xl font-bold tracking-tight text-foreground">Verify your email</h1>
        <p className="mt-2 text-muted-foreground">
          Enter the verification code sent to your email address.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {error && (
          <div className="rounded-lg border border-destructive/50 bg-destructive/10 p-4 text-sm text-destructive">
            {error}
          </div>
        )}

        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <Input
            id="email"
            type="email"
            placeholder="you@example.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            className="h-12"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="code">Verification Code</Label>
          <Input
            id="code"
            type="text"
            placeholder="Enter your code"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            required
            className="h-12"
          />
        </div>

        <Button 
          type="submit" 
          className="h-12 w-full bg-primary text-primary-foreground hover:bg-primary/90"
          disabled={isLoading}
        >
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Verifying...
            </>
          ) : (
            "Verify email"
          )}
        </Button>
      </form>

      <p className="text-center text-sm text-muted-foreground">
        {"Didn't receive the code? "}
        <button type="button" className="font-semibold text-secondary hover:text-secondary/80">
          Resend code
        </button>
      </p>
    </div>
  )
}

export default function VerifyPage() {
  return (
    <Suspense fallback={
      <div className="flex h-40 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    }>
      <VerifyForm />
    </Suspense>
  )
}
