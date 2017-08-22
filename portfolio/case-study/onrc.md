---
title: Case Study - ONRC IAM Implementation
name: Case Study - ONRC IAM Implementation
image: https://www.onrc.ro/templates/ict_conches_free/images/onrc_65.png
author: Cristian Chiru
description: IAM Implementation for the Romanian National Trade Register Office
---

![The Romanian National Trade Register Office](https://www.onrc.ro/templates/ict_conches_free/images/onrc_65.png)

# The customer

The Romanian National Trade Register Office (ONRC) is a public institution with legal personality, organized under the subordination of the Romanian Ministry of Justice having the following functions:

- legal publicity
- record of the legal and financial situation of all businesses
- economic and statistical information
- commercial information
- simplification of the procedures for registration and authorization for operation of companies
- information and assistance

# The challenge

In an effort to improve the service quality, ONRC decided to incorporate the functionality of hundred of independent systems and applications into a more compact architecture. Following a complete redesign and reconstruction of all the ONRC core applications, a solution was needed to provide the authentication and authorization of the new applications in a centralized and easy to manage system.

However, due to availability reasons, all the regional sites were designed to have the applications deployed locally and use synchronization mechanisms to make data available from and to the ONRC HQ. This meant in fact that every Romanian county had to have its own infrastructure and application deployment, together with the security solution.

The challenge was to implement a complete authentication and authorization system based on Oracle products in such a manner that it would not require any major architectural or applications model changes.

The bigger challenge was to achieve all that for the HQ, a standby site, Bucharest office and all 41 Romanian counties in 

less than 5 calendar months.

# Achievements

## General

- The solution went live together with the main ONRC systems (*)
- Held workshops and training sessions with the key users
- Documented the installation, the configuration and created user guides

## Infrastructure

- Installed Oracle Identity and Access Management 11g plus the Directory Services 11g in high availability (everything doubled) for all 44 environments
- Configured custom Oracle Internet Directory LDAP level synchronization. The data is pushed one way, from the HQ to all regional OID cluster instances.

## Identity Management

- Configured Oracle Identity Manager centrally to be the single point of trust for user creation and provisioning
- Performed initial user import
- Configured and defined an initial set of application roles to be assigned to the users

## Access Management

- Configured access policies for ONRC application authentication
- Configured Single Sign-On to integrate all the user applications, including in-house developed and Oracle built.

(*) Due to a severe bug in the Oracle Access Manager at the time, the Single Sign-on Phase did not go live, it remained to be further tested on the test environment and be deployed at a later date.

# Value

- Cleaning and consolidation of data sources, fully automated account provisioning based on predefined authorization rules
- Ease and speed of propagating system changes throughout the regional sites.
- No service disruption for regional offices in case the central site becomes unavailable
- Delegated administration: ability to assign administrators for each regional site.
- Productivity, risk mitigation, cost reduction, increased service levels, and improved transparency
