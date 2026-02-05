"use client"

import React, { useEffect, useState, Suspense } from "react"
import Link from "next/link"
import { usePathname } from "next/navigation"
import { Button } from "@/components/ui/button"
import { verifyUser } from "@/lib/auth"
import { Loader2, Check, XCircle, Mail } from "lucide-react"

function VerifyContent() {
  const pathname = usePathname()
  
  // Parse email and token from path: /verifyuser/email/token
  const segments = pathname.split('/').filter(Boolean)
  const emailIndex = segments.indexOf('verifyuser') + 1
  const email = segments[emailIndex] ? decodeURIComponent(segments[emailIndex]) : ""
  const token = segments[emailIndex + 1] || ""

  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState("")
  const [success, setSuccess] = useState(false)

  useEffect(() => {
    const performVerification = async () => {
      if (!email || !token) {
        setError("Invalid verification link. Missing email or token.")
        setIsLoading(false)
        return
      }

      try {
        const result = await verifyUser(email, token)

        if (result.success) {
          setSuccess(true)
        } else {
          setError(result.message || "Verification failed. The link may be expired or invalid.")
        }
      } catch (err) {
        setError("A network error occurred. Please try again later.")
      } finally {
        setIsLoading(false)
      }
    }

    performVerification()
  }, [email, token])

  if (isLoading) {
    return (
      <div className="w-full max-w-md space-y-8 text-center">
        <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-primary/20">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
        <h1 className="text-3xl font-bold tracking-tight text-foreground">Verifying your email</h1>
        <p className="text-muted-foreground">
          Please wait while we verify your account...
        </p>
      </div>
    )
  }

  if (success) {
    return (
      <div className="w-full max-w-md space-y-8 text-center">
        <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-secondary/20">
          <Check className="h-8 w-8 text-secondary" />
        </div>
        <h1 className="text-3xl font-bold tracking-tight text-foreground">Email verified!</h1>
        <p className="text-muted-foreground">
          Your email <strong>{email}</strong> has been successfully verified. You can now sign in to your account.
        </p>
        <Button asChild className="w-full bg-primary text-primary-foreground hover:bg-primary/90">
          <Link href="/login">Continue to Login</Link>
        </Button>
      </div>
    )
  }

  return (
    <div className="w-full max-w-md space-y-8 text-center">
      <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-destructive/20">
        <XCircle className="h-8 w-8 text-destructive" />
      </div>
      <h1 className="text-3xl font-bold tracking-tight text-foreground">Verification failed</h1>
      <div className="rounded-lg border border-destructive/50 bg-destructive/10 p-4 text-sm text-destructive text-left">
        {error}
      </div>
      <p className="text-muted-foreground">
        If you believe this is an error, you can try to verify manually by entering the code in the verification page.
      </p>
      <div className="flex flex-col gap-4">
        <Button asChild className="w-full bg-primary text-primary-foreground hover:bg-primary/90">
          <Link href={`/verify?email=${encodeURIComponent(email)}`}>Verify manually</Link>
        </Button>
        <Button asChild variant="outline" className="w-full">
          <Link href="/login">Back to Login</Link>
        </Button>
      </div>
    </div>
  )
}

export default function AutomatedVerifyPage() {
  return (
    <Suspense fallback={<Loader2 className="h-8 w-8 animate-spin text-primary" />}>
      <VerifyContent />
    </Suspense>
  )
}
