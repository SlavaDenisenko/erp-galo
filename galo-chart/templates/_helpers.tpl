{{/*
Expand the name of the chart.
*/}}
{{- define "galo.name" -}}
{{- $root := . -}}
{{- default $root.Chart.Name $root.Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "galo.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "galo.chart" -}}
{{- $root := . -}}
{{- printf "%s-%s" $root.Chart.Name $root.Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Retrieve a value from a nested YAML structure. If the specified path does not exist
or the value is nil, return the provided default value.

Parameters:
- context: The starting YAML object (e.g., .Values.services)
- path: A list of keys representing the path to the desired value
- default: The global context where the value will be retrieved if it's not found in the local context
*/}}
{{- define "getOrDefault" -}}
{{- $service := .local -}}
{{- $global := .global -}}
{{- $path := .path -}}
{{- $value := $service -}}
{{- range $key := $path }}
{{- if $value -}}
{{- $value = (index $value $key | default nil) -}}
{{- end }}
{{- end }}
{{- if not $value -}}
{{- $value = $global -}}
{{- range $key := $path }}
{{- $value = (index $value $key) -}}
{{- end }}
{{- end }}
{{- $value -}}
{{- end }}

{{/*
Create a complete template URL to connect to the database
*/}}
{{- define "databaseUrl" -}}
{{- $service := .local -}}
{{- $global := .global -}}
{{- $host := $service.database.service.name -}}
{{- $port := int ($service.database.service.port | default $global.database.service.port) -}}
{{- $dbName := $service.database.name -}}
{{- printf "jdbc:postgresql://%s:%d/%s" $host $port $dbName -}}
{{- end }}

{{/*
Create a complete template image name for the database
*/}}
{{- define "databaseImage" -}}
{{- $service := .local -}}
{{- $global := .global -}}
{{- $repository := include "getOrDefault" (dict "local" $service "global" $global "path" (list "database" "image" "repository")) -}}
{{- $tag := include "getOrDefault" (dict "local" $service "global" $global "path" (list "database" "image" "tag")) -}}
{{- printf "%s:%s" $repository $tag -}}
{{- end }}

{{/*
Create a complete template image name for the application
*/}}
{{- define "applicationImage" -}}
{{- $service := . -}}
{{- $repository := $service.application.image.repository -}}
{{- $tag := $service.application.image.tag -}}
{{- printf "%s:%s" $repository $tag -}}
{{- end }}

{{/*
Create a complete image name for the database migration tool
*/}}
{{- define "migrationImage" -}}
{{- $global := . -}}
{{- $repository := $global.database.migration.image.repository -}}
{{- $tag := $global.database.migration.image.tag -}}
{{- printf "%s:%s" $repository $tag -}}
{{- end }}

{{/*
Common labels
*/}}
{{- define "galo.labels" -}}
{{- $root := . -}}
helm.sh/chart: {{ include "galo.chart" $root }}
{{ include "galo.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ $root.Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "galo.selectorLabels" -}}
{{- $root := . -}}
app.kubernetes.io/name: {{ include "galo.name" $root }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
