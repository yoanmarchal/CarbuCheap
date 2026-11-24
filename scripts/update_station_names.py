#!/usr/bin/env python3
"""
Regenerates app/src/main/assets/station_names.tsv from the
"Référentiel des noms et enseignes de stations-service (enrichi par OpenStreetMap)"
published by Chiffrex on data.gouv.fr (ODbL 1.0, updated every Sunday).

Run before each release:
    python scripts/update_station_names.py

Output: one "<id_station_officiel>\t<nom_normalise>" line per station, UTF-8.
The data is only subset (two columns), never modified, so no share-alike obligation;
the attribution is shown in the app's "Sources et licences" dialog.
"""
import csv
import io
import sys
import urllib.request
from pathlib import Path

# Stable data.gouv.fr redirect to the latest version of the resource
RESOURCE_URL = "https://www.data.gouv.fr/fr/datasets/r/0207ded0-2d19-47af-b1a6-62915ec1b721"
OUTPUT = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "assets" / "station_names.tsv"


def main() -> int:
    request = urllib.request.Request(RESOURCE_URL, headers={"User-Agent": "carbucheap-build-script"})
    with urllib.request.urlopen(request, timeout=60) as response:
        text = response.read().decode("utf-8")

    reader = csv.DictReader(io.StringIO(text))
    missing = {"id_station_officiel", "nom_normalise"} - set(reader.fieldnames or [])
    if missing:
        print(f"Unexpected CSV columns, missing: {missing}", file=sys.stderr)
        return 1

    rows = []
    for row in reader:
        station_id = (row["id_station_officiel"] or "").strip()
        name = " ".join((row["nom_normalise"] or "").split())  # also strips tabs/newlines
        if station_id.isdigit() and name:
            rows.append((int(station_id), name))

    if len(rows) < 5000:
        print(f"Only {len(rows)} rows: refusing to overwrite the asset", file=sys.stderr)
        return 1

    rows.sort()
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_text("".join(f"{i}\t{n}\n" for i, n in rows), encoding="utf-8", newline="\n")
    print(f"Wrote {len(rows)} station names to {OUTPUT}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
